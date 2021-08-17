package ak.znetwork.znpcservers.npc;

/** Enumerates all possible toggles for a npc. */
public enum ToggleType {
    GLOW(true) {
        @Override
        protected void onToggle(NPC npc, String toggleValue) {
            npc.doToggleGlow(toggleValue);
        }
    },
    HOLO(false),
    MIRROR(false),
    LOOK(false);

    /**
     * {@code true} If should update the npc after running {@link #doToggle(NPC, String)}.
     */
    private final boolean autoUpdate;

    /**
     * Creates a new {@link ToggleType}.
     */
    ToggleType(boolean autoUpdate) {
        this.autoUpdate = autoUpdate;
    }

    /**
     * Finds the toggle value on a npc for the given {@link ToggleType}.
     *
     * @param npc The npc to find the value on.
     * @param toggleType The toggle type.
     * @return {@code true} If the found value is true.
     */
    public static boolean isTrue(NPC npc, ToggleType toggleType) {
        return npc.getNpcPojo().getNpcToggleValues().getOrDefault(toggleType, false);
    }

    /**
     * @see #autoUpdate
     */
    public boolean isAutoUpdate() {
        return autoUpdate;
    }

    /**
     * Runs {@link #onToggle(NPC, String)} and also changes the corresponding values
     * in the npc toggle map.
     * @see #doToggle(NPC, String)
     */
    public void doToggle(NPC npc, String toggleValue) {
        npc.getNpcPojo().getNpcToggleValues().put(this, !npc.getNpcPojo()
                .getNpcToggleValues().getOrDefault(this, false));
        if (isAutoUpdate()) {
            onToggle(npc, toggleValue);
            npc.deleteViewers(); // self update
        }
    }

    /**
     * The toggle type function.
     *
     * @param npc The npc to execute that function for.
     * @param toggleValue The optional value for the toggle, i.e for getting a glow color, etc ..
     * @throws UnsupportedOperationException If this toggle type does not provide a function.
     */
    protected void onToggle(NPC npc, String toggleValue) {
        throw new UnsupportedOperationException();
    }
}
