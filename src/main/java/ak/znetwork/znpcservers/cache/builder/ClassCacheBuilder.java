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
public class ClassCacheBuilder implements ClassCacheImpl.Builder {

    /**
     * A empty string.
     */
    private static final String EMPTY_STRING = "";

    /**
     * A empty array.
     */
    private static final Class<?>[] EMPTY_ARRAY = ArrayUtils.EMPTY_CLASS_ARRAY;

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
    private final Class<?>[] parameterTypes;

    /**
     * Creates a new empty classCache builder.
     */
    public ClassCacheBuilder() {
        this(PackageType.DEFAULT,
                EMPTY_STRING,
                EMPTY_STRING,
                EMPTY_STRING,
                EMPTY_ARRAY
        );
    }

    /**
     * Creates a new classCache builder.
     *
     * @param packageType     The class package.
     * @param className       The class name.
     * @param methodName      The class method name.
     * @param fieldName       The class field name.
     * @param parameterTypes  The class parameters.
     */
    protected ClassCacheBuilder(PackageType packageType,
                                String className,
                                String methodName,
                                String fieldName,
                                Class<?>... parameterTypes) {
        this.packageType = packageType;
        this.className = className;
        this.methodName = methodName;
        this.fieldName = fieldName;
        this.parameterTypes = parameterTypes;
    }

    @Override
    public ClassCacheBuilder packageType(PackageType packageType) {
        return new ClassCacheBuilder(packageType,
                className,
                methodName,
                fieldName,
                parameterTypes
        );
    }

    @Override
    public ClassCacheBuilder className(String className) {
        return new ClassCacheBuilder(packageType,
                toBukkitClass(className),
                methodName,
                fieldName,
                parameterTypes
        );
    }

    @Override
    public ClassCacheBuilder methodName(String methodName) {
        return new ClassCacheBuilder(packageType,
                className,
                methodName,
                fieldName,
                parameterTypes
        );
    }

    @Override
    public ClassCacheBuilder fieldName(String fieldName) {
        return new ClassCacheBuilder(packageType,
                className,
                methodName,
                fieldName,
                parameterTypes
        );
    }

    @Override
    public ClassCacheBuilder parameterTypes(Class<?>... parameterTypes) {
        return new ClassCacheBuilder(packageType,
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
        return String.format(packageType.getPackageName() + ".%s", className);
    }
}
