package io.github.gonalez.znpcservers.npc.internal.function;

import com.google.common.collect.ImmutableList;
import io.github.gonalez.znpcservers.npc.Npc;
import io.github.gonalez.znpcservers.npc.function.AbstractNpcFunction;
import io.github.gonalez.znpcservers.npc.function.NpcFunctionContext;
import io.github.gonalez.znpcservers.npc.function.NpcFunctionValue;
import io.github.gonalez.znpcservers.npc.function.ValidateNpcFunctionException;

/**
 * An {@link EmptyNpcFunction} that overrides the {@link #validateContext(NpcFunctionContext)} method for
 * checking if the context {@link NpcFunctionContext#attributes()}, are non nulls and they are assignable
 * for each of the required function {@link AbstractNpcFunction#requiredValues}.
 *
 * @author Gaston Gonzalez {@literal <znetworkw.dev@gmail.com>}
 */
public class SimpleValidateSavedNpcFunction extends SelfUpdateSavedNpcFunction {
    public SimpleValidateSavedNpcFunction(String name, ImmutableList<NpcFunctionValue<?>> requiredValues) {
        super(name, requiredValues);
    }

    @Override
    protected void validateContext(NpcFunctionContext functionContext)
            throws ValidateNpcFunctionException {
        super.validateContext(functionContext);
        for (NpcFunctionValue<?> functionValue : requiredValues) {
            Object mapValue = functionContext.attributes().get(functionValue.name());
            if (mapValue == null) {
                throw new ValidateNpcFunctionException("can't find value for " + functionValue.name());
            }
            if (!mapValue.getClass().isAssignableFrom(functionValue.getType())) {
                throw new ValidateNpcFunctionException(
                    mapValue.getClass().getSimpleName() + " should be a subclass of " +
                        functionValue.getType().getSimpleName());
            }
        }
    }

    @Override
    public NpcFunctionContext resolve(Npc npc) {
        return new DefaultNpcContextBuilder()
            ./*if has attributes*/merge(npc.getModel().getFunctions().get(getName()).getAttributes())
            .build();
    }
}
