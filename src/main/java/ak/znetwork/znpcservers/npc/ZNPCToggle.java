package ak.znetwork.znpcservers.npc;

/**
 * <p>Copyright (c) ZNetwork, 2020.</p>
 *
 * @author ZNetwork
 * @since 07/02/2020
 */
public enum ZNPCToggle {
    /**
     * Determines if a hologram of an npc should be seen.
     */
    HOLO {
        @Override
        public void toggle(ZNPC npc, String value) {
            npc.getNpcPojo().setHasToggleHolo(!npc.getNpcPojo().isHasToggleHolo());

            if (!npc.getNpcPojo().isHasToggleHolo()) {
                npc.getViewers().forEach(player -> npc.getHologram().delete(player));
            } else {
                npc.getViewers().forEach(player -> npc.getHologram().spawn(player));
            }
        }
    },

    /**
     * Determines if the glow of an npc should be seen.
     */
    GLOW {
        @Override
        public void toggle(ZNPC npc, String value) {
            npc.getNpcPojo().setHasGlow(!npc.getNpcPojo().isHasGlow());
            npc.toggleGlow(value);

            // Update new glow color
            npc.deleteViewers();
        }
    },

    /**
     * Determines if the skin of an npc should be the same as the viewers.
     */
    MIRROR {
        @Override
        public void toggle(ZNPC npc, String value) {
            npc.getNpcPojo().setHasMirror(!npc.getNpcPojo().isHasMirror());
        }
    },

    /**
     * Determines if an npc should look at viewers.
     */
    LOOK {
        @Override
        public void toggle(ZNPC npc, String value) {
            npc.getNpcPojo().setHasLookAt(!npc.getNpcPojo().isHasLookAt());
        }
    };

    /**
     * The toggle function.
     *
     * @param npc The npc to run the toggle function to.
     * @param value The optional toggle value.
     */
    public abstract void toggle(ZNPC npc, String value);
}
