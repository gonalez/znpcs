package ak.znetwork.znpcservers.cache.impl;

import ak.znetwork.znpcservers.cache.enums.PackageType;

/**
 * <p>Copyright (c) ZNetwork, 2020.</p>
 *
 * @author ZNetwork
 * @since 07/02/2020
 */
public interface ClassCacheImpl {

    /**
     * {@inheritDoc}
     */
    <T> T typeOf();

    interface Builder<T> {

        /**
         * The class type.
         */
        Builder<T> builderType(BuilderType builderType);

        /**
         * The package of the class.
         */
        Builder<T> packageType(PackageType packageType);

        /**
         * The class name.
         */
        Builder<T> className(String className);

        /**
         * The method name.
         */
        Builder<T> methodName(String methodName);

        /**
         * The field name.
         */
        Builder<T> fieldName(String fieldName);

        /**
         * The class parameters.
         */
        Builder<T> parameterTypes(Class... parameters);

        /**
         * {@inheritDoc}
         */
        enum BuilderType {

            /**
             * {@inheritDoc}
             */
            DEFAULT,

            /**
             * {@inheritDoc}
             */
            CLASS,

            /**
             * {@inheritDoc}
             */
            METHOD,

            /**
             * {@inheritDoc}
             */
            CONSTRUCTOR,

            /**
             * {@inheritDoc}
             */
            FIELD
        }
    }
}
