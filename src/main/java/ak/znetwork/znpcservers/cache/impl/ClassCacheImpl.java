package ak.znetwork.znpcservers.cache.impl;

import ak.znetwork.znpcservers.cache.builder.ClassCacheBuilder;

import com.google.common.collect.Iterables;
import org.bukkit.Bukkit;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * <p>Copyright (c) ZNetwork, 2020.</p>
 *
 * @author ZNetwork
 * @since 07/02/2020
 */
public interface ClassCacheImpl {

    /**
     * A cache for storing loaded classes.
     */
    class ClassCache {

        /**
         * A map containing the cached objects & classes.
         */
        protected static final ConcurrentMap<CacheKey, Object> CACHE = new ConcurrentHashMap<>();

        /**
         * Locates a cached type by its name.
         *
         * @param name        The type class name.
         * @param objectClass The type class.
         * @return The cached object or {@code null} if no type was found.
         */
        public static Object find(String name, Class<?> objectClass) {
            return CACHE.get(new CacheKey(name, objectClass));
        }

        /**
         * Registers a new type key into the cache.
         *
         * @param name        The type class name.
         * @param object      The type value.
         * @param objectClass The type class.
         */
        public static void register(String name, Object object, Class<?> objectClass) {
            Object findObject = CACHE.get(new CacheKey(name, objectClass));
            if (findObject != null)
                return;

            CACHE.putIfAbsent(new CacheKey(name, objectClass), object);
        }

        /**
         * A cache key for storing a class type.
         */
        private static class CacheKey {

            /**
             * The key class type.
             */
            private final Class<?> type;

            /**
             * The key name.
             */
            private final String value;

            /**
             * Creates a new cache key.
             *
             * @param value The key type name.
             * @param type  The key class type.
             */
            public CacheKey(String value,
                            Class<?> type) {
                this.type = type;
                this.value = value;
            }

            @Override
            public boolean equals(Object o) {
                if (this == o) return true;
                if (o == null || getClass() != o.getClass()) return false;
                CacheKey classKey = (CacheKey) o;
                return Objects.equals(type, classKey.type) && Objects.equals(value, classKey.value);
            }

            @Override
            public int hashCode() {
                return Objects.hash(type, value);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    interface Builder {

        /**
         * The package of the class.
         */
        Builder packageType(String packageName);

        /**
         * The class name.
         */
        Builder className(String className);

        /**
         * The class name.
         */
        Builder className(Class<?> clazz);

        /**
         * The method name.
         */
        Builder methodName(String methodName);

        /**
         * The field name.
         */
        Builder fieldName(String fieldName);

        /**
         * The class parameters.
         */
        Builder parameterTypes(Class<?>... parameters);
    }


    /**
     * An abstract implementation of {@code ClassCacheBuilder}.
     *
     * @param <T> The loaded type.
     */
    abstract class Default<T> extends ClassCacheBuilder {

        /**
         * The logger.
         */
        private static final Logger LOGGER = Bukkit.getLogger();

        /**
         * The builder class.
         */
        protected Class<?> BUILDER_CLASS;

        /**
         * {@inheritDoc}
         */
        protected Default(ClassCacheBuilder cacheBuilder) {
            super(cacheBuilder.getPackageName(),
                    cacheBuilder.getClassName(),
                    cacheBuilder.getMethodName(),
                    cacheBuilder.getFieldName(),
                    cacheBuilder.getParameterTypes()
            );
        }

        /**
         * Returns the loaded type.
         *
         * @return The loaded class.
         */
        public T typeOf() {
            try {
                BUILDER_CLASS = Class.forName(getClassName());
                return onLoad();
            } catch (Throwable throwable) {
                // Skip class...
                log(
                        "Skipping cache for " + getClassName()
                );
                return null;
            }
        }

        /**
         * Sends debug message to console.
         *
         * @param message The message to send.
         */
        private void log(String message) {
            LOGGER.log(Level.WARNING, message);
        }

        /**
         * Loads the class type.
         *
         * @return The loaded class type.
         */
        protected abstract T onLoad() throws Exception;

        /**
         * Initializes and loads the given class.
         */
        public static class ClazzLoader extends Default<Class<?>> {

            /**
             * Creates a new class loader for the given builder.
             */
            public ClazzLoader(ClassCacheBuilder cacheBuilder) {
                super(cacheBuilder);
            }

            @Override
            protected Class<?> onLoad() {
                return BUILDER_CLASS;
            }
        }

        /**
         * Initializes and loads the given method.
         */
        public static class MethodLoader extends Default<Method> {

            /**
             * Creates a new method loader for the given builder.
             */
            public MethodLoader(ClassCacheBuilder cacheBuilder) {
                super(cacheBuilder);
            }

            @Override
            protected Method onLoad() throws NoSuchMethodException {
                return Iterables.size(getParameterTypes()) > 0 ?
                        BUILDER_CLASS.getDeclaredMethod(getMethodName(), Iterables.get(getParameterTypes(), 0)) :
                        BUILDER_CLASS.getDeclaredMethod(getMethodName());
            }
        }

        /**
         * Initializes and loads the given field.
         */
        public static class FieldLoader extends Default<Field> {

            /**
             * Creates a new field loader for the given builder.
             */
            public FieldLoader(ClassCacheBuilder cacheBuilder) {
                super(cacheBuilder);
            }

            @Override
            protected Field onLoad() throws NoSuchFieldException {
                Field field = BUILDER_CLASS.getDeclaredField(getFieldName());
                field.setAccessible(true);
                return field;
            }
        }

        /**
         * Initializes and loads the given constructor.
         */
        public static class ConstructorLoader extends Default<Constructor<?>> {

            /**
             * Creates a new constructor loader for the given builder.
             */
            public ConstructorLoader(ClassCacheBuilder cacheBuilder) {
                super(cacheBuilder);
            }

            @Override
            protected Constructor<?> onLoad() throws NoSuchMethodException {
                Constructor<?> constructor = null;
                if (Iterables.size(getParameterTypes()) > 1) { // 1.17>>>>>><<<<3.3..<
                    for (Class<?>[] keyParameters : getParameterTypes()) {
                        try {
                            constructor = BUILDER_CLASS.getDeclaredConstructor(keyParameters);
                        } catch (NoSuchMethodException e) {
                            // Next...
                        }
                    }

                } else {
                    constructor = BUILDER_CLASS.getDeclaredConstructor(Iterables.get(getParameterTypes(), 0));
                }

                // Set accessible
                if (constructor != null) {
                    constructor.setAccessible(true);
                }
                return constructor;
            }
        }

        /**
         * Initializes and loads the enum constants for the given class.
         */
        public static class EnumLoader extends Default<Enum<?>[]> {

            /**
             * Creates a new multiple field loader for the given builder.
             */
            public EnumLoader(ClassCacheBuilder cacheBuilder) {
                super(cacheBuilder);
            }

            @Override
            protected Enum<?>[] onLoad() {
                Enum<?>[] enumConstants = (Enum<?>[]) BUILDER_CLASS.getEnumConstants();
                for (Enum<?> enumConstant : enumConstants) {
                    // Register value
                    ClassCache.register(enumConstant.name(), enumConstant, BUILDER_CLASS);
                }
                return enumConstants;
            }
        }
    }
}
