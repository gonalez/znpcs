package ak.znetwork.znpcservers.cache.impl;

import ak.znetwork.znpcservers.cache.builder.ClassCacheBuilder;
import ak.znetwork.znpcservers.cache.enums.PackageType;

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
        Builder packageType(PackageType packageType);

        /**
         * The class name.
         */
        Builder className(String className);

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
     * {@inheritDoc}
     */
    abstract class Default extends ClassCacheBuilder {

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
            super(cacheBuilder.getPackageType(),
                    cacheBuilder.getClassName(),
                    cacheBuilder.getMethodName(),
                    cacheBuilder.getFieldName(),
                    cacheBuilder.getParameterTypes()
            );
        }

        /**
         * Returns the loaded type.
         *
         * @param <T> The class type.
         * @return The loaded class.
         */
        public <T> T typeOf() {
            try {
                BUILDER_CLASS = Class.forName(getClassName());
                return (T) onLoad();
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
         */
        private void log(String message) {
            LOGGER.log(Level.WARNING, message);
        }

        /**
         * Loads the class type.
         *
         * @return The loaded class type.
         */
        protected abstract Object onLoad() throws Throwable;

        /**
         * Initializes and loads the given class.
         */
        public static class ClassLoader extends Default {

            /**
             * Creates a new class loader for the given builder.
             */
            public ClassLoader(ClassCacheBuilder cacheBuilder) {
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
        public static class MethodLoader extends Default {

            /**
             * Creates a new method loader for the given builder.
             */
            public MethodLoader(ClassCacheBuilder cacheBuilder) {
                super(cacheBuilder);
            }

            @Override
            protected Method onLoad() {
                try {
                    return BUILDER_CLASS.getDeclaredMethod(getMethodName(), getParameterTypes());
                } catch (NoSuchMethodException e) {
                    throw new IllegalStateException("Cannot find method " + getMethodName());
                }
            }
        }

        /**
         * Initializes and loads the given field.
         */
        public static class FieldLoader extends Default {

            /**
             * Creates a new field loader for the given builder.
             */
            public FieldLoader(ClassCacheBuilder cacheBuilder) {
                super(cacheBuilder);
            }

            @Override
            protected Field onLoad() {
                try {
                    Field field = BUILDER_CLASS.getDeclaredField(getFieldName());
                    field.setAccessible(true);
                    return field;
                } catch (NoSuchFieldException e) {
                    throw new IllegalStateException("Cannot find field " + getFieldName());
                }
            }
        }

        /**
         * Initializes and loads the given constructor.
         */
        public static class ConstructorLoader extends Default {

            /**
             * Creates a new constructor loader for the given builder.
             */
            public ConstructorLoader(ClassCacheBuilder cacheBuilder) {
                super(cacheBuilder);
            }

            @Override
            protected Constructor<?> onLoad() {
                try {
                    return BUILDER_CLASS.getDeclaredConstructor(getParameterTypes());
                } catch (NoSuchMethodException e) {
                    throw new IllegalStateException("Cannot find constructor for class " + getClassName());
                }
            }
        }

        /**
         * Initializes and loads the enums for the given class.
         */
        public static class MultipleLoad extends Default {

            /**
             * Creates a new multiple field loader for the given builder.
             */
            public MultipleLoad(ClassCacheBuilder cacheBuilder) {
                super(cacheBuilder);
            }

            @Override
            protected Field[] onLoad() {
                final Field[] fields = BUILDER_CLASS.getFields();
                for (Field field : fields) {
                    if (!field.isEnumConstant())
                        continue;

                    try {
                        ClassCache.register(field.getName(), field.get(null), BUILDER_CLASS);
                    } catch (IllegalAccessException e) {
                        throw new IllegalStateException("Cannot load field " + field.getName());
                    }
                }
                return fields;
            }
        }
    }
}
