package io.github.znetworkw.znpcservers.npc;

/**
 * Represents a npc function.
 */
public abstract class NPCFunction {
    /**
     * Calls {@link NPCFunction#function(NPC, String)} for the given npc.
     *
     * @param npc The npc to run the function for.
     * @param data The optional data i.e for getting glow color, etc...
     */
    protected abstract void function(NPC npc, String data);

    /**
     * Returns the function name prefix.
     *
     * @return The function name prefix.
     */
    public abstract String name();

    /**
     * Returns {@code true} if can run {@link #function(NPC, String)}
     */
    public abstract boolean allow(NPC npc);

    /**
     * Runs {@link #function(NPC, String)}
     * if {@link #allow(NPC)}.
     */
    public void doRunFunction(NPC npc,
                              String data) {
        if (!allow(npc)) {
            return;
        }
        function(npc, data);
    }

    /**
     * Locates the npc value for this function.
     * @see FunctionFactory#isTrue(NPC, String)
     */
    public boolean isTrue(NPC npc) {
        return FunctionFactory.isTrue(npc, this);
    }

    /**
     * A {@link NPCFunction} with a empty implementation
     * for {@link NPCFunction#function(NPC, String)}.
     */
    public static class WithoutFunction extends NPCFunction {
        /** The function name. */
        private final String name;

        /**
         * Creates a new npc function with the given name.
         *
         * @param name The name
         */
        public WithoutFunction(String name) {
            this.name = name;
        }

        @Override
        protected void function(NPC npc, String data) {
            // no implementation
        }

        @Override
        public String name() {
            return name;
        }

        @Override
        public boolean allow(NPC npc) {
            return true;
        }
    }

    /**
     * A {@link NPCFunction} with a default implementation
     * for {@link NPCFunction#function(NPC, String)} that updates the npc for viewers.
     */
    public static class WithoutFunctionSelfUpdate extends WithoutFunction {
        /**
         * Creates a new npc function with the given name.
         *
         * @param name The name
         */
        public WithoutFunctionSelfUpdate(String name) {
            super(name);
        }

        @Override
        protected void function(NPC npc, String data) {
            npc.deleteViewers();
        }
    }
}
