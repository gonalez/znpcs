package io.github.znetworkw.znpcservers.npc;

/**
 * Used to determine the position of a item when equipping a {@link NPC}.
 */
public enum ItemSlot {
    HELMET(5),
    CHESTPLATE(4),
    LEGGINGS(3),
    BOOTS(2),
    OFFHAND(1),
    HAND(0);

    /** The equipment slot id. */
    private final int slot;

    /** The equipment slot id for oldest versions. 1.8 */
    private final int slotOld;

    /**
     * Creates a new equipment identifier.
     *
     * @param slot The equipment id.
     */
    ItemSlot(int slot) {
        this.slot = slot;
        this.slotOld = slot-1;
    }

    /**
     * Returns the equipment position.
     */
    public int getSlot() {
        return slot;
    }

    /**
     * Returns the equipment position for oldest versions.
     */
    public int getSlotOld() {
        return slotOld;
    }
}