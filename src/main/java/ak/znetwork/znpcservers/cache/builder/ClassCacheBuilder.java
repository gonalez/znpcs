package ak.znetwork.znpcservers.cache.builder;

import ak.znetwork.znpcservers.cache.impl.ClassCacheImpl;

import com.google.common.collect.ImmutableList;
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
     * The class name.
     */
    private final String packageName, className, methodName, fieldName;

    /**
     * The class.
     */
    private final Class<?> clazz;

    /**
     * The class parameters.
     */
    private final ImmutableList<Class<?>[]> parameterTypes;

    /**
     * Creates a new empty classCache builder.
     */
    public ClassCacheBuilder() {
        this(EMPTY_STRING,
                EMPTY_STRING,
                EMPTY_STRING,
                EMPTY_STRING,
                ImmutableList.of(EMPTY_ARRAY)
        );
    }

    /**
     * Creates a new classCache builder.
     *
     * @param packageType    The class package.
     * @param className      The class name.
     * @param methodName     The class method name.
     * @param fieldName      The class field name.
     * @param parameterTypes The class parameters.
     */
    protected ClassCacheBuilder(String packageType,
                                String className,
                                String methodName,
                                String fieldName,
                                ImmutableList<Class<?>[]> parameterTypes) {
        this.packageName = packageType;
        this.className = className;
        this.methodName = methodName;
        this.fieldName = fieldName;
        this.parameterTypes = parameterTypes;
        this.clazz = null;
    }

    @Override
    public ClassCacheBuilder packageType(String packageName) {
        return new ClassCacheBuilder(packageName,
                className,
                methodName,
                fieldName,
                parameterTypes
        );
    }

    @Override
    public ClassCacheBuilder className(String className) {
        return new ClassCacheBuilder(packageName,
                toBukkitClass(className),
                methodName,
                fieldName,
                parameterTypes
        );
    }

    @Override
    public ClassCacheBuilder className(Class<?> clazz) {
        return new ClassCacheBuilder(packageName,
                clazz == null ? EMPTY_STRING : clazz.getName(),
                methodName,
                fieldName,
                parameterTypes
        );
    }

    @Override
    public ClassCacheBuilder methodName(String methodName) {
        return new ClassCacheBuilder(packageName,
                className,
                methodName,
                fieldName,
                parameterTypes
        );
    }

    @Override
    public ClassCacheBuilder fieldName(String fieldName) {
        return new ClassCacheBuilder(packageName,
                className,
                methodName,
                fieldName,
                parameterTypes
        );
    }

    @Override
    public ClassCacheBuilder parameterTypes(Class<?>... to) {
        return new ClassCacheBuilder(packageName,
                className,
                methodName,
                fieldName,
                // ><!>>1.17.1><<<>>
                ImmutableList.<Class<?>[]>builder().addAll(
                        ImmutableList.of(to)
                ).build()
        );
    }

    /**
     * {@inheritDoc}
     */
    protected String toBukkitClass(String className) {
        return packageName.length() > 0 ? String.format(packageName + ".%s", className) :
                className;
    }
}
