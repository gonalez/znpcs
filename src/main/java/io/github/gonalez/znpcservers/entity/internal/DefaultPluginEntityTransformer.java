package io.github.gonalez.znpcservers.entity.internal;

import com.google.gson.internal.Primitives;
import io.github.gonalez.znpcservers.entity.PluginEntityTypeData;
import io.github.gonalez.znpcservers.entity.PluginEntityTypeTransformer;

/**
 * @author Gaston Gonzalez {@literal <znetworkw.dev@gmail.com>}
 */
public class DefaultPluginEntityTransformer implements PluginEntityTypeTransformer<Object> {
    public static PluginEntityTypeTransformer<Object> INSTANCE = new DefaultPluginEntityTransformer();

    @Override
    public Object transform(PluginEntityTypeData data, Object... arguments) throws Exception {
        final Class<?>[] parameterTypes = data.getEntityConstructor().getParameterTypes();
        if (arguments.length >= parameterTypes.length) {
            for (int i = 0; i < parameterTypes.length; i++) {
                if (!Primitives.wrap(parameterTypes[i]).isAssignableFrom(arguments[i].getClass())) {
                    throw new IllegalStateException(
                        String.format("expected type: %s , got: %s",
                            parameterTypes[i].getName(), arguments[i].getClass().getName()));
                }
            }
        } else {
            throw new IllegalArgumentException(
                String.format("invalid arguments size: %s, expected %s", arguments.length, parameterTypes.length));
        }
        return data.getEntityConstructor().newInstance(arguments);
    }
}
