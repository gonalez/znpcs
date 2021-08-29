package io.github.znetworkw.znpcservers.npc.packet;

import com.google.common.collect.ImmutableMap;
import io.github.znetworkw.znpcservers.npc.NPC;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Used for caching a {@link Packet} methods for an {@link NPC}.
 * <p>
 * Methods annotated with {@link PacketValue} will be cached on the first call.
 * To flush the cache of a method use {@link #flushCache(String...)}, otherwise the method
 * will return the first call return value.
 */
public class PacketCache {

    protected static final ImmutableMap<Method, PacketValue> VALUE_LOOKUP_BY_NAME;

    static {
        ImmutableMap.Builder<Method, PacketValue> methodPacketValueBuilder = ImmutableMap.builder();
        for (Method method : Packet.class.getMethods()) {
            if (!method.isAnnotationPresent(PacketValue.class)) {
                continue;
            }
            methodPacketValueBuilder.put(method, method.getAnnotation(PacketValue.class));
        }
        VALUE_LOOKUP_BY_NAME = methodPacketValueBuilder.build();
    }

    /** Cache for method results. */
    private final Map<String, Object> packetResultCache = new ConcurrentHashMap<>();
    /** The proxy instance. */
    private final Packet proxyInstance;
    /** The npc for which the methods will be invoked & cached. */
    private final NPC npc;

    /**
     * Creates a new packet cache for the given npc.
     *
     * @param npc The npc.
     * @throws AssertionError If cannot instantiate the proxy instance for the packets.
     */
    public PacketCache(NPC npc) {
        this.npc = npc;
        try {
            this.proxyInstance = newProxyInstance(PacketFactory.PACKET_FOR_CURRENT_VERSION.getClass().newInstance());
        } catch (InstantiationException | IllegalAccessException e) {
            throw new AssertionError("Unable to create proxy instance for npc: " + npc.getNpcPojo().getId(), e);
        }
    }

    /**
     * Returns the proxy instance of the packets instance
     * for the current bukkit version.
     *
     * @return The proxy instance of the packets instance
     * for the current bukkit version.
     */
    public Packet getProxyInstance() {
        return proxyInstance;
    }

    /**
     * Creates a new proxy instance for the given packet instance.
     *
     * @param packet The instance for which the proxy will be created.
     * @return A new proxy instance for the given packet instance.
     */
    protected Packet newProxyInstance(Packet packet) {
        return (Packet) Proxy.newProxyInstance(
            packet.getClass().getClassLoader(),
            new Class[]{Packet.class},
            new PacketHandler(this, packet));
    }

    /**
     * Creates a cache for the given method result or returns the cached result.
     *
     * @param instance The non proxied packet instance.
     * @param npc
     *    The npc to process the name that
     *    will be used in the cache key.
     * @param method The method.
     * @param args The method arguments.
     * @return The cached method result.
     * @throws IllegalStateException If the method does not have a {@link PacketValue} annotation.
     * @throws AssertionError If cannot invoke the given method.
     */
    private Object getOrCache(Packet instance,
                              NPC npc,
                              Method method,
                              Object[] args) {
        if (!VALUE_LOOKUP_BY_NAME.containsKey(method)) {
            throw new IllegalStateException("value not found for method: " + method.getName());
        }
        final PacketValue packetValue = VALUE_LOOKUP_BY_NAME.get(method);
        final String keyString = packetValue.valueType().resolve(packetValue.keyName(), npc, args);
        return packetResultCache.computeIfAbsent(
            keyString,
            o -> {
                try {
                    return method.invoke(instance, args);
                } catch (InvocationTargetException | IllegalAccessException operationException) {
                    throw new AssertionError("can't invoke method: " + method.getName(), operationException);
                }
            }
        );
    }

    /**
     * Flushes all cached methods if these
     * startsWith any of the given {@code strings}.
     * <p>
     * <b>NOTE:</b> Strings must match the {@link PacketValue#keyName()}
     * of the cached method, it should not be the name of the method.
     *
     * @param strings Method key names to flush.
     * @see PacketValue
     */
    public void flushCache(String... strings) {
        final Set<Map.Entry<String, Object>> set = packetResultCache.entrySet();
        for (String string : strings) {
            set.removeIf(entry -> entry.getKey().startsWith(string));
        }
    }

    /**
     * @see #flushCache(String...)
     */
    public void flushCache() {
        flushCache(VALUE_LOOKUP_BY_NAME.values().stream()
            .map(PacketValue::keyName).toArray(String[]::new));
    }

    /**
     * A {@link InvocationHandler} that caches and invoke the packet methods.
     */
    private static class PacketHandler implements InvocationHandler {
        /** The packet cache. */
        private final PacketCache packetCache;
        /** The packets instance. non-proxied */
        private final Packet packets;

        /**
         * Creates a new {@link PacketHandler} for a packet instance.
         */
        public PacketHandler(PacketCache packetCache,
                             Packet packets) {
            this.packetCache = packetCache;
            this.packets = packets;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if (VALUE_LOOKUP_BY_NAME.containsKey(method)) {
                // get the cached packet or create it
                return packetCache.getOrCache(packets, packetCache.npc, method, args);
            }
            return method.invoke(packets, args);
        }
    }
}