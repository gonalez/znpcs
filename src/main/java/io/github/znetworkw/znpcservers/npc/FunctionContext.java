package io.github.znetworkw.znpcservers.npc;

/** Context class that contains the necessary
 * information for running a npc function. */
public interface FunctionContext {
    /** The context npc. */
    NPC getNPC();

    /** Interface for defining a value for the context. */
    interface WithValue extends FunctionContext {
        String getValue();
    }

    /** Base class for the context. */
    class DefaultContext implements FunctionContext {
        /** The npc */
        private final NPC npc;

        /** Creates a new function context for the given npc. */
        public DefaultContext(NPC npc) {
            this.npc = npc;
        }

        @Override
        public NPC getNPC() {
            return npc;
        }
    }

    /** Class that implements {@link WithValue}. */
    class ContextWithValue extends DefaultContext implements WithValue {
        /** The context value. */
        private final String value;

        /** Creates a new function context with the given value. */
        public ContextWithValue(NPC npc, String value) {
            super(npc);
            this.value = value;
        }

        @Override
        public String getValue() {
            return value;
        }
    }
}
