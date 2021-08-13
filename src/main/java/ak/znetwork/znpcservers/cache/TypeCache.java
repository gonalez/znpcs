package ak.znetwork.znpcservers.cache;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import lombok.Getter;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @inheritDoc
 */
public interface TypeCache {
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
     * A builder for the {@code AbstractCache} class.
     */
    @Getter
    class CacheBuilder {
        /**
         * A empty string.
         */
        private static final String EMPTY_STRING = "";

        /**
         * The class package.
         */
        private final CachePackage cachePackage;

        /**
         * The class package category.
         */
        private final CacheCategory cacheCategory;

        /**
         * The class name.
         */
        private final String className, methodName, fieldName;

        /**
         * Additional data for package.
         */
        private final String additionalData;

        /**
         * The class.
         */
        private final Class<?> clazz;

        /**
         * The class parameters.
         */
        private final Iterable<Class<?>[]> parameterTypes;

        /**
         * Creates a new {@link CacheBuilder} with the provided package.
         *
         * @param cachePackage The cache package.
         */
        public CacheBuilder(CachePackage cachePackage) {
            this(cachePackage,
                    CacheCategory.NONE,
                    EMPTY_STRING,
                    EMPTY_STRING,
                    EMPTY_STRING,
                    EMPTY_STRING,
                    ImmutableList.of());
        }

        /**
         * Creates a new {@link CacheBuilder} with the provided values.
         *
         * @param cachePackage   The class package.
         * @param cacheCategory  The class category.
         * @param className      The class name.
         * @param methodName     The class method name.
         * @param fieldName      The class field name.
         * @param additionalData The package additional data.
         * @param parameterTypes The class parameters.
         */
        protected CacheBuilder(CachePackage cachePackage,
                               CacheCategory cacheCategory,
                               String className,
                               String methodName,
                               String fieldName,
                               String additionalData,
                               Iterable<Class<?>[]> parameterTypes) {
            this.cachePackage = cachePackage;
            this.cacheCategory = cacheCategory;
            this.className = className;
            this.methodName = methodName;
            this.fieldName = fieldName;
            this.additionalData = additionalData;
            this.parameterTypes = parameterTypes;
            this.clazz = null;
        }

        /**
         * Defines the category of the cache.
         *
         * @param cacheCategory The category.
         * @return The builder with the new category.
         */
        public CacheBuilder withCategory(CacheCategory cacheCategory) {
            return new CacheBuilder(
                    cachePackage,
                    cacheCategory,
                    className,
                    methodName,
                    fieldName,
                    additionalData,
                    parameterTypes
            );
        }

        /**
         * Defines the cache class name.
         *
         * @param className The class name.
         * @return The builder with the new class.
         */
        public CacheBuilder withClassName(String className) {
            return new CacheBuilder(
                    cachePackage,
                    cacheCategory,
                    formatClass(className),
                    methodName,
                    fieldName,
                    additionalData,
                    parameterTypes
            );
        }

        /**
         * Defines the cache class.
         *
         * @param clazz The class.
         * @return The builder with the new class.
         */
        public CacheBuilder withClassName(Class<?> clazz) {
            return new CacheBuilder(
                    cachePackage,
                    cacheCategory,
                    clazz == null ? EMPTY_STRING : clazz.getName(),
                    methodName,
                    fieldName,
                    additionalData,
                    parameterTypes
            );
        }

        /**
         * Defines the cache method.
         *
         * @param methodName The method name.
         * @return The builder with the new method.
         */
        public CacheBuilder withMethodName(String methodName) {
            return new CacheBuilder(
                    cachePackage,
                    cacheCategory,
                    className,
                    methodName,
                    fieldName,
                    additionalData,
                    parameterTypes
            );
        }

        /**
         * Defines the cache field.
         *
         * @param fieldName The field name.
         * @return The builder with the new field.
         */
        public CacheBuilder withFieldName(String fieldName) {
            return new CacheBuilder(
                    cachePackage,
                    cacheCategory,
                    className,
                    methodName,
                    fieldName,
                    additionalData,
                    parameterTypes
            );
        }

        /**
         * Defines the additional data for the package.
         *
         * @param additionalData The additional data.
         * @return The builder with the new package additional data.
         */
        public CacheBuilder withAdditionalData(String additionalData) {
            return new CacheBuilder(
                    cachePackage,
                    cacheCategory,
                    className,
                    methodName,
                    fieldName,
                    additionalData,
                    parameterTypes
            );
        }

        /**
         * Defines the parameter types for the cache class.
         *
         * @param types The parameter types.
         * @return The builder with the new cache class parameter types.
         */
        public CacheBuilder withParameterTypes(Class<?>... types) {
            return new CacheBuilder(
                    cachePackage,
                    cacheCategory,
                    className,
                    methodName,
                    fieldName,
                    additionalData,
                    Iterables.concat(parameterTypes, ImmutableList.of(types))
            );
        }

        /**
         * {@inheritDoc}
         */
        protected String formatClass(String className) {
            switch (cachePackage) {
                case MINECRAFT_SERVER:
                case CRAFT_BUKKIT:
                    return String.format((cachePackage == CachePackage.CRAFT_BUKKIT ?
                            cachePackage.getFixedPackageName() : cachePackage.getForCategory(cacheCategory, additionalData)) + ".%s",
                            className);
                case DEFAULT:
                    return className;
                default:
                    throw new IllegalArgumentException("Unexpected package " + cachePackage.name());
            }
        }
    }

    /**
     * An abstract implementation.
     *
     * @param <T> The class type.
     */
    abstract class BaseCache<T> {
        /**
         * The logger.
         */
        private static final Logger LOGGER = Logger.getLogger(BaseCache.class.getName());

        /**
         * The builder class.
         */
        protected Class<?> BUILDER_CLASS;

        /**
         * The builder.
         */
        protected final CacheBuilder cacheBuilder;

        /**
         * Creates a new cache loader for the given builder.
         */
        protected BaseCache(CacheBuilder cacheBuilder) {
            this.cacheBuilder = cacheBuilder;
        }

        /**
         * Returns the loaded type.
         *
         * @return The loaded type class.
         */
        public T load() {
            try {
                BUILDER_CLASS = Class.forName(cacheBuilder.getClassName());
                return onLoad();
            } catch (Throwable throwable) {
                // Skip class...
                log(
                        "Skipping cache for " + cacheBuilder.getClassName()
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
        public static class ClazzLoader extends BaseCache<Class<?>> {
            /**
             * Creates a new class loader for the given builder.
             */
            public ClazzLoader(CacheBuilder cacheBuilder) {
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
        public static class MethodLoader extends BaseCache<Method> {
            /**
             * Creates a new method loader for the given builder.
             */
            public MethodLoader(CacheBuilder builder) {
                super(builder);
            }

            @Override
            protected Method onLoad() throws NoSuchMethodException {
                return Iterables.size(cacheBuilder.getParameterTypes()) > 0 ?
                        BUILDER_CLASS.getDeclaredMethod(cacheBuilder.getMethodName(), Iterables.get(cacheBuilder.getParameterTypes(), 0)) :
                        BUILDER_CLASS.getDeclaredMethod(cacheBuilder.getMethodName());
            }
        }

        /**
         * Initializes and loads the given field.
         */
        public static class FieldLoader extends BaseCache<Field> {
            /**
             * Creates a new field loader for the given builder.
             */
            public FieldLoader(CacheBuilder cacheBuilder) {
                super(cacheBuilder);
            }

            @Override
            protected Field onLoad() throws NoSuchFieldException {
                Field field = BUILDER_CLASS.getDeclaredField(cacheBuilder.getFieldName());
                field.setAccessible(true);
                return field;
            }
        }

        /**
         * Initializes and loads the given constructor.
         */
        public static class ConstructorLoader extends BaseCache<Constructor<?>> {
            /**
             * Creates a new constructor loader for the given builder.
             */
            public ConstructorLoader(CacheBuilder cacheBuilder) {
                super(cacheBuilder);
            }

            @Override
            protected Constructor<?> onLoad() throws NoSuchMethodException {
                Constructor<?> constructor = null;
                if (Iterables.size(cacheBuilder.getParameterTypes()) > 1) { // 1.17>>>>>><<<<3.3..<
                    for (Class<?>[] keyParameters : cacheBuilder.getParameterTypes()) {
                        try {
                            constructor = BUILDER_CLASS.getDeclaredConstructor(keyParameters);
                        } catch (NoSuchMethodException e) {
                            // Next...
                        }
                    }
                } else {
                    constructor = Iterables.size(cacheBuilder.getParameterTypes()) > 0 ? BUILDER_CLASS.getDeclaredConstructor(Iterables.get(cacheBuilder.getParameterTypes(), 0)) : BUILDER_CLASS.getDeclaredConstructor();
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
        public static class EnumLoader extends BaseCache<Enum<?>[]> {
            /**
             * Creates a new multiple field loader for the given builder.
             */
            public EnumLoader(CacheBuilder cacheBuilder) {
                super(cacheBuilder);
            }

            @Override
            protected Enum<?>[] onLoad() {
                Enum<?>[] enumConstants = (Enum<?>[]) BUILDER_CLASS.getEnumConstants();
                for (Enum<?> enumConstant : enumConstants) {
                    ClassCache.register(enumConstant.name(), enumConstant, BUILDER_CLASS);
                }
                return enumConstants;
            }
        }
    }
}
