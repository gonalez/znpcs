package ak.znetwork.znpcservers.npc;

import ak.znetwork.znpcservers.cache.CachePackage;
import ak.znetwork.znpcservers.cache.CacheType;

import com.google.common.collect.Iterables;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

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
    private final Map<String, Method> methods;

    /**
     * Creates a new customization processor for an entity type.
     *
     * @param entityType The entity type.
     * @param methodsName The methods to load.
     */
    public CustomizationProcessor(EntityType entityType,
                                  Iterable<String> methodsName) {
        this(entityType.getEntityClass(), methodsName);
    }

    /**
     * Creates a new customization processor for an entity type.
     *
     * @param entityClass The bukkit entity type class.
     * @param methodsName The methods to load.
     */
    protected CustomizationProcessor(Class<? extends Entity> entityClass,
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
    protected Map<String, Method> getMethods(Iterable<String> iterable) {
        Map<String, Method> builder = new HashMap<>();
        for (Method method : entityClass.getMethods()) {
            if (builder.containsKey(method.getName()) ||
                    !Iterables.contains(iterable, method.getName())) {
                // only load provided methods..
                continue;
            }
            for (Class<?> parameter : method.getParameterTypes()) {
                TypeProperty typeProperty = TypeProperty.forType(parameter);
                if (typeProperty == null
                        && parameter.isEnum()) {
                    // create a new cache for the values on the enum class for late use
                    new CacheType.AbstractCache.EnumLoader(
                            new CacheType.CacheBuilder(CachePackage.DEFAULT)
                            .withClassName(parameter.getTypeName())).load();
                }
            }
            builder.put(method.getName(), method);
        }
        return builder;
    }

    /**
     * Returns {@code true} if a method with the given name exists.
     *
     * @param name The method name.
     * @return If a method with the given name exists.
     */
    public boolean contains(String name) {
        return methods.containsKey(name);
    }

    /**
     * Returns A map containing the loaded provided entity methods.
     *
     * @return A map containing the loaded provided entity methods.
     */
    public Map<String, Method> getMethods() {
        return methods;
    }
}