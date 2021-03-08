package ak.znetwork.znpcservers.cache.builder;

import ak.znetwork.znpcservers.cache.enums.PackageType;
import ak.znetwork.znpcservers.cache.impl.ClassCacheImpl;

import org.apache.commons.lang.ArrayUtils;

import lombok.Getter;

/**
 * <p>Copyright (c) ZNetwork, 2020.</p>
 *
 * @author ZNetwork
 * @since 07/02/2020
 */
@Getter
public class ClassCacheBuilder<T> implements ClassCacheImpl.Builder<T> {

    /**
     * A empty string.
     */
    private static final String EMPTY_STRING = "";

    /**
     * A empty array.
     */
    private static final Class[] EMPTY_ARRAY = ArrayUtils.EMPTY_CLASS_ARRAY;

    /**
     * The builder type.
     */
    private final BuilderType builderType;

    /**
     * The class package.
     */
    private final PackageType packageType;

    /**
     * The class name.
     */
    private final String className, methodName, fieldName;

    /**
     * The class parameters.
     */
    private final Class[] parameterTypes;

    /**
     * Creates a new empty classCache builder.
     */
    public ClassCacheBuilder() {
        this(BuilderType.DEFAULT,
                PackageType.DEFAULT,
                EMPTY_STRING,
                EMPTY_STRING,
                EMPTY_STRING,
                EMPTY_ARRAY
        );
    }

    /**
     * Creates a new classCache builder.
     *
     * @param builderType     The class type.
     * @param packageType     The class package.
     * @param className       The class name.
     * @param methodName      The class method name.
     * @param fieldName       The class field name.
     * @param parameterTypes  The class parameters.
     */
    protected ClassCacheBuilder(BuilderType builderType,
                                PackageType packageType,
                                String className,
                                String methodName,
                                String fieldName,
                                Class... parameterTypes) {
        this.builderType = builderType;
        this.packageType = packageType;
        this.className = className;
        this.methodName = methodName;
        this.fieldName = fieldName;
        this.parameterTypes = parameterTypes;
    }

    @Override
    public ClassCacheImpl.Builder<T> builderType(BuilderType builderType) {
        return new ClassCacheBuilder<T>(builderType,
                packageType,
                className,
                methodName,
                fieldName,
                parameterTypes
        );
    }

    @Override
    public ClassCacheImpl.Builder<T> packageType(PackageType packageType) {
        return new ClassCacheBuilder<T>(builderType,
                packageType,
                className,
                methodName,
                fieldName,
                parameterTypes
        );
    }

    @Override
    public ClassCacheImpl.Builder<T> className(String className) {
        return new ClassCacheBuilder<T>(builderType,
                packageType,
                toBukkitClass(className),
                methodName,
                fieldName,
                parameterTypes
        );
    }

    @Override
    public ClassCacheImpl.Builder<T> methodName(String methodName) {
        return new ClassCacheBuilder<T>(builderType,
                packageType,
                className,
                methodName,
                fieldName,
                parameterTypes
        );
    }

    @Override
    public ClassCacheImpl.Builder<T> fieldName(String fieldName) {
        return new ClassCacheBuilder<T>(builderType,
                packageType,
                className,
                methodName,
                fieldName,
                parameterTypes
        );
    }

    @Override
    public ClassCacheImpl.Builder<T> parameterTypes(Class... parameterTypes) {
        return new ClassCacheBuilder<T>(builderType,
                packageType,
                className,
                methodName,
                fieldName,
                parameterTypes
        );
    }

    /**
     * {@inheritDoc}
     */
    private String toBukkitClass(String className) {
        return String.format(getPackageType().getPackageName() + ".%s", className);
    }
}
