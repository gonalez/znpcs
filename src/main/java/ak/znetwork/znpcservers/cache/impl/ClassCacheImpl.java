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
     * The logger.
     */
    Logger LOGGER = Bukkit.getLogger();

    /**
     * A cache for storing loaded classes.
     */
    class ClassCache {

        /**
         * A map containing the cached objects & classes.
         */
        protected static final ConcurrentMap<ClassKey, Object> cache;

        static {
            // Initialize map
            cache = new ConcurrentHashMap<>();
        }

        /**
         * Finds a cached object by its name.
         *
         * @param name        The object class name.
         * @param objectClass The object class.
         * @return            The cached object or {@code null} if no object was found.
         */
        public static Object find(String name, Class<?> objectClass) {
            return cache.get(new ClassKey(name, objectClass));
        }

        /**
         * Caches a new object.
         *
         * @param name        The object class name.
         * @param object      The object.
         * @param objectClass The object class.
         */
        public static void register(String name, Object object, Class<?> objectClass) {
            Object findObject = cache.get(new ClassKey(name, objectClass));
            if (findObject != null)
                return;

            cache.putIfAbsent(new ClassKey(name, objectClass), object);
        }

        /**
         * {@inheritDoc}
         */
        static class ClassKey {

            /**
             * The key class type.
             */
            private final Class<?> type;

            /**
             * The key name.
             */
            private final String value;

            /**
             * Creates a new class key.
             *
             * @param value The key name.
             * @param type  The key class type.
             */
            public ClassKey(String value,
                            Class<?> type) {
                this.type = type;
                this.value = value;
            }

            @Override
            public boolean equals(Object o) {
                if (this == o) return true;
                if (o == null || getClass() != o.getClass()) return false;
                ClassKey classKey = (ClassKey) o;
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
         * The default class type.
         */
        protected Object TYPE = null;

        /**
         * The builder class.
         */
        protected final Class<?> BUILDER_CLASS = getBuilderClass();

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
         * The builder class.
         *
         * @return The class defined by the builder @className.
         */
        protected Class<?> getBuilderClass() {
            try {
                return Class.forName(getClassName());
            } catch (ClassNotFoundException e) {
                log();
            }
            return null;
        }

        /**
         * Sends debug message to console.
         */
        public void log() {
            LOGGER.log(Level.WARNING, String.format("Skipping cache for %s", getName()));
        }

        /**
         * {@inheritDoc}
         */
        public String getName() {
            return getClassName();
        }

        /**
         * Loads the class type.
         *
         * @return The loaded class type.
         */
        public abstract Object typeOf();

        /**
         * Initializes & loads the given class.
         */
        public static class ClassLoader extends Default {

            /**
             * Creates a new class loader for the given builder.
             */
            public ClassLoader(ClassCacheBuilder cacheBuilder) {
                super(cacheBuilder);
            }

            @Override
            public Class<?> typeOf() {
                return getBuilderClass();
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
            public Method typeOf() {
                try {
                    return getBuilderClass().getDeclaredMethod(getMethodName(), getParameterTypes());
                } catch (NoSuchMethodException e) {
                    log();
                }
                return null;
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
            public Field typeOf() {
                try {
                    Field field = getBuilderClass().getDeclaredField(getFieldName());
                    field.setAccessible(true);

                    return field;
                } catch (NoSuchFieldException e) {
                    log();
                }
                return null;
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
            public Constructor<?> typeOf() {
                try {
                    return getBuilderClass().getDeclaredConstructor(getParameterTypes());
                } catch (NoSuchMethodException e) {
                    log();
                }
                return null;
            }
        }

        /**
         * Initializes and loads the enums in the given class.
         */
        public static class MultipleLoad extends Default {

            /**
             * Creates a new multiple field loader for the given builder.
             */
            public MultipleLoad(ClassCacheBuilder cacheBuilder) {
                super(cacheBuilder);
            }

            @Override
            public Object typeOf() {
                for (Field field : BUILDER_CLASS.getFields()) {
                    if (!field.isEnumConstant())
                        continue;

                    try {
                        ClassCache.register(field.getName(), field.get(TYPE), BUILDER_CLASS);
                    } catch (IllegalAccessException e) {
                        throw new IllegalStateException(String.format("Cannot load field %s", field.getName()));
                    }
                }
                return TYPE;
            }
        }
    }
}
