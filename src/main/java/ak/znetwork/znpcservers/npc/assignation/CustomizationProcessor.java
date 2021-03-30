package ak.znetwork.znpcservers.npc.assignation;

import ak.znetwork.znpcservers.cache.builder.ClassCacheBuilder;
import ak.znetwork.znpcservers.cache.enums.PackageType;
import ak.znetwork.znpcservers.cache.impl.ClassCacheImpl;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;

import org.bukkit.entity.Entity;

import java.lang.reflect.Method;

/**
 * <p>Copyright (c) ZNetwork, 2020.</p>
 *
 * @author ZNetwork
 * @since 07/02/2020
 */
public class CustomizationProcessor {

    /**
     * The bukkit entity class.
     */
    private final Class<? extends Entity> entityClass;

    /**
     * A map containing the loaded provided entity methods.
     */
    private final ImmutableMap<String, Method> methods;

    /**
     * Creates a new customization processor for an entity type.
     *
     * @param entityClass The bukkit entity type class.
     * @param methodsName The methods to load.
     */
    public CustomizationProcessor(Class<? extends Entity> entityClass,
                                  Iterable<String> methodsName) {
        this.entityClass = entityClass;
        this.methods = getMethods(methodsName);
    }

    /**
     * Returns a map of all loaded methods that were provided.
     *
     * @param iterable The method names.
     * @return A map of all loaded methods that were provided.
     */
    protected ImmutableMap<String, Method> getMethods(Iterable<String> iterable) {
        ImmutableMap.Builder<String, Method> builder =
                ImmutableMap.builder();
        for (Method method : entityClass.getMethods()) {
            if (!Iterables.contains(iterable, method.getName())) {
                // Only load provided methods..
                continue;
            }
            for (Class<?> parameter : method.getParameterTypes()) {
                TypeProperty typeProperty = TypeProperty.forType(parameter);
                if (typeProperty == null
                        && parameter.isEnum()) {
                    // Create a new cache for the constants on the enum class,
                    // for later use.
                    new ClassCacheImpl.Default.EnumLoader(new ClassCacheBuilder().
                            packageType(PackageType.DEFAULT).
                            className(parameter.getTypeName())).
                            typeOf();
                }
            }
            builder.put(method.getName(), method);
        }
        return builder.build();
    }

    /**
     * Checks if a method exists.
     *
     * @param name The method name.
     * @return {@code true} If method found.
     */
    public boolean contains(String name) {
        return methods.containsKey(name);
    }

    /**
     * Returns A map containing the loaded provided entity methods.
     *
     * @return A map containing the loaded provided entity methods.
     */
    public ImmutableMap<String, Method> getMethods() {
        return methods;
    }
}