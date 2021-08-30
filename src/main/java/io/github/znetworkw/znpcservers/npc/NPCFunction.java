package io.github.znetworkw.znpcservers.npc;

/**
 * Represents a npc function.
 */
public abstract class NPCFunction {
    public enum ResultType {
        SUCCESS,
        FAIL
    }

    /** The function name. */
    private final String name;

    /**
     * Creates a new npc function with the given name.
     *
     * @param name The name
     */
    public NPCFunction(String name) {
        this.name = name;
    }

    /**
     * Returns the function name.
     *
     * @return The function name.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns {@code true} if can run the function.
     */
    protected abstract boolean allow(NPC npc);

    /**
     * Runs the function for the given npc.
     *
     * @param npc The npc to run the function for.
     */
    protected abstract ResultType runFunction(NPC npc, FunctionContext functionContext);

    /**
     * Runs {@link NPCFunction#runFunction(NPC, FunctionContext)}
     * if {@link #allow(NPC)} for the given npc.
     *w
     * @param npc The npc to run the function for.
     */
    public void doRunFunction(NPC npc, FunctionContext functionContext) {
        if (!allow(npc)) {
            return;
        }
        ResultType resultType = runFunction(npc, functionContext);
        if (resultType == ResultType.SUCCESS) {
            npc.getNpcPojo().getFunctions().put(getName(), !isTrue(npc));
        }
    }

    /** Auto resolves the function with a context for the npc. */
    protected ResultType resolve(NPC npc) {
        throw new IllegalStateException("resolve is not implemented.");
    }
    
    /**
     * Locates the npc value for this function.
     *
     * @see FunctionFactory#isTrue(NPC, String)
     */
    public boolean isTrue(NPC npc) {
        return FunctionFactory.isTrue(npc, this);
    }

    /**  Class that has a empty implementation for the function. */
    public static class WithoutFunction extends NPCFunction {
        public WithoutFunction(String name) {
            super(name);
        }

        @Override
        protected ResultType runFunction(NPC npc, FunctionContext functionContext) {
            return ResultType.SUCCESS; /*empty*/
        }

        @Override
        protected boolean allow(NPC npc) {
            return true;
        }

        @Override
        protected ResultType resolve(NPC npc) {
            return ResultType.SUCCESS;
        }
    }

    /** Class that updates the npc when calling the function. */
    public static class WithoutFunctionSelfUpdate extends WithoutFunction {
        public WithoutFunctionSelfUpdate(String name) {
            super(name);
        }

        @Override
        protected ResultType runFunction(NPC npc, FunctionContext functionContext) {
            npc.deleteViewers();
            return ResultType.SUCCESS;
        }
    }
}
