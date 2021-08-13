package ak.znetwork.znpcservers.npc;

/**
 * Used to determine the position of a item when equipping a {@link NPC}.
 */
public enum ItemSlot {
    HELMET(4, 5),
    CHESTPLATE(3, 4),
    LEGGINGS(2, 3),
    BOOTS(1, 2),
    OFFHAND(0, 1),
    HAND(0, 0);

    /** The equipment slot id for oldest versions. */
    private final int slotOld;

    /** The equipment slot id for newer versions. */
    private final int slotNew;

    /**
     * Creates a new equipment identifier.
     *
     * @param slotOld The equipment id for oldest versions.
     * @param slotNew The equipment id for newer versions.
     */
    ItemSlot(int slotOld,
             int slotNew) {
        this.slotOld = slotOld;
        this.slotNew = slotNew;
    }

    /**
     * Returns the equipment position for newer versions.
     */
    public int getSlotNew() {
        return slotNew;
    }

    /**
     * Returns the equipment position for oldest versions.
     */
    public int getSlotOld() {
        return slotOld;
    }
}