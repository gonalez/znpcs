package io.github.gonalez.znpcservers.npc.function;

import com.google.common.collect.ImmutableList;
import io.github.gonalez.znpcservers.npc.Npc;

/**
 * A base abstract class for the {@link NpcFunction} interface that provides convenience methods to
 * validate a context before running the {@link NpcFunction#executeFunction(Npc, NpcFunctionContext)}.
 *
 * @author Gaston Gonzalez {@literal <znetworkw.dev@gmail.com>}
 */
public abstract class AbstractNpcFunction implements NpcFunction {
    /**
     * The function name.
     */
    private final String name;

    /**
     * A list of necessary values to run this function.
     * <p>
     * Should be checked in the {@link #validateContext(NpcFunctionContext)} method to check if the provided
     * context attributes are valid when calling {@link NpcFunction#executeFunction(Npc, NpcFunctionContext)}.
     */
    protected final ImmutableList<NpcFunctionValue<?>> requiredValues;

    /**
     * Constructs a new {@link AbstractNpcFunction} for the specified name.
     *
     * @param name the function name.
     */
    public AbstractNpcFunction(String name) {
        this(name, ImmutableList.of());
    }

    /**
     * Constructs a new {@link AbstractNpcFunction} for the specified name and required values.
     *
     * @param name the function name.
     * @param requiredValues the necessary values to run this function.
     */
    public AbstractNpcFunction(String name, ImmutableList<NpcFunctionValue<?>> requiredValues) {
        this.name = name;
        this.requiredValues = requiredValues;
    }

    /**
     * Returns the unique function name.
     *
     * @return the function name.
     */
    public String getName() {
        return name;
    }

    /**
     * Invoked before running the function for validating the context of this function.
     * <p>
     * Subclasses are expected to override this method to perform their own validation
     * depending on the function.
     *
     * @param functionContext the function context to validate.
     * @throws ValidateNpcFunctionException if cannot validate the function.
     */
    protected void validateContext(NpcFunctionContext functionContext)
        throws ValidateNpcFunctionException {}

    /**
     * Executes the function for the given npc.
     *
     * @param functionContext the function context.
     * @param npc the npc to run the function for.
     * @throws Exception if the function cannot be executed.
     */
    protected abstract void function(Npc npc, NpcFunctionContext functionContext) throws Exception;

    /**
     * Returns {@code true} if should save the given function context
     * for the npc when running {@link #executeFunction(Npc, NpcFunctionContext)}.
     *
     * @return {@code true} if the context should be saved.
     */
    protected abstract boolean isSaveAllowed();

    /**
     * Executes and validates the {@link #function(Npc, NpcFunctionContext)} method for the given npc.
     * <p>
     * Before executing the function, the method will check if the given context is valid using
     * {@link #validateContext(NpcFunctionContext)}, if the validation fails the method will thrown an
     * {@link ValidateNpcFunctionException}, as described in that method.
     *
     * @param npc the npc to run that function for.
     * @param functionContext the function context.
     * @see #validateContext(NpcFunctionContext)
     */
    public void executeFunction(Npc npc, NpcFunctionContext functionContext)
            throws ExecuteNpcFunctionException, ValidateNpcFunctionException {
        validateContext(functionContext);
        if (isSaveAllowed()) {
            npc.getModel().getFunctions().put(name, new NpcFunctionModel(functionContext, !isTrue(npc)));
        }
        try {
            function(npc, functionContext);
        } catch (Exception exception) {
            throw new ExecuteNpcFunctionException("cannot execute function for npc");
        }
    }

    /**
     * Returns the npc value for this function.
     * @see NpcFunctions#isTrue(Npc, String)
     */
    public boolean isTrue(Npc npc) {
        return NpcFunctions.isTrue(npc, this);
    }
}
