package ak.znetwork.znpcservers.cache;

import ak.znetwork.znpcservers.cache.builder.ClassCacheBuilder;
import ak.znetwork.znpcservers.cache.impl.ClassCacheImpl;
import com.google.common.collect.HashBasedTable;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.common.collect.Table;
import org.bukkit.Bukkit;

import lombok.Getter;

import static ak.znetwork.znpcservers.cache.impl.ClassCacheImpl.Builder.BuilderType;

/**
 * <p>Copyright (c) ZNetwork, 2020.</p>
 *
 * @author ZNetwork
 * @since 07/02/2020
 */
@Getter
public final class ClassCache implements ClassCacheImpl {

    /**
     * The logger.
     */
    private static final Logger logger = Bukkit.getLogger();

    /**
     * A collection that serves as a cache for classes.
     */
    private static final Table<String, Class<?>, Object> classCache;

    static {
        classCache = HashBasedTable.create();
    }

    /**
     * The loaded types.
     */
    private final ClassCacheBuilder<?> cacheBuilder;

    /**
     * The class type.
     */
    private Object TYPE = null;

    /**
     * Creates a new cache for builder.
     *
     * @param builder The type builder.
     */
    public ClassCache(Builder<?> builder) {
        this.cacheBuilder = (ClassCacheBuilder<?>) builder;
    }

    @Override
    public <T> T typeOf() {
        BuilderType builderType = getCacheBuilder().getBuilderType();

        try {
            Class<?> classType = Class.forName(getCacheBuilder().getClassName());

            if (builderType == BuilderType.CLASS) {
                TYPE = classType;
            } else {
                if (builderType == Builder.BuilderType.CONSTRUCTOR) {
                    TYPE = classType.getDeclaredConstructor(getCacheBuilder().getParameterTypes());
                } else if (builderType == Builder.BuilderType.METHOD) {
                    TYPE = classType.getDeclaredMethod(getCacheBuilder().getMethodName(), getCacheBuilder().getParameterTypes());
                } else {
                    Field field = classType.getDeclaredField(getCacheBuilder().getFieldName());
                    field.setAccessible(true);

                    TYPE = field;
                }
            }
            return (T) TYPE;
        } catch (ClassNotFoundException | NoSuchFieldException | NoSuchMethodException e) {
            logger.log(Level.WARNING, String.format("Skipping cache for %s %s", builderType.name(), builderType == BuilderType.CLASS | builderType == BuilderType.CONSTRUCTOR ? cacheBuilder.getClassName() : builderType == BuilderType.METHOD ? cacheBuilder.getMethodName() : cacheBuilder.getFieldName()));
        }
        return null;
    }
}
