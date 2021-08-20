package ak.znetwork.znpcservers.npc;

import java.util.EnumSet;

/** Enumerates all possible toggles for a npc. */
public enum ToggleType {
    GLOW {
        @Override
        protected void toggle(NPC npc, String toggleValue) {
            npc.doToggleGlow(toggleValue);
        }
    },
    HOLO,
    MIRROR,
    LOOK;

    /** valid toggle types */
    private static final EnumSet<ToggleType> TOGGLE_COMPATIBLE = EnumSet.of(GLOW);

    /**
     * Returns {@code true} if this is toggle compatible.
     */
    public boolean isToggleCompatible() {
        return TOGGLE_COMPATIBLE.contains(this);
    }

    /**
     * Returns the toggle value for the given npc if it exists, false otherwise.
     *
     * @param npc The npc to find the value on.
     * @param toggleType The toggle type.
     * @return The toggle value for the given npc if it exists, false otherwise.
     */
    public static boolean isTrue(NPC npc, ToggleType toggleType) {
        return npc.getNpcPojo().getToggleValues().getOrDefault(toggleType, false);
    }

    /**
     * Runs {@link #toggle(NPC, String)} for the given npc and also
     * changes the corresponding values in the npc toggle map.
     * @see #toggle(NPC, String)
     */
    public void doToggle(NPC npc, String toggleValue) {
        npc.getNpcPojo().getToggleValues().put(this, !isTrue(npc, this));
        if (isToggleCompatible()) {
            toggle(npc, toggleValue);
        }
        npc.deleteViewers(); /*self update*/
    }

    /**
     * Calls {@link ToggleType#toggle(NPC, String)} for the given npc.
     *
     * @param npc The npc to run that function for.
     * @param toggleValue The optional value i.e for getting glow color, etc...
     * @throws UnsupportedOperationException If {@code this} not implements this function.
     */
    protected void toggle(NPC npc, String toggleValue) {
        throw new UnsupportedOperationException("toggle is not implemented for: " + this);
    }
}
