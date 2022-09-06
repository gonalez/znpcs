package io.github.gonalez.znpcs.entity;

/**
 * Enumerates all possible item slots when equipping an npc.
 */
public enum PluginEntityEquipmentSlot {
    HELMET(5),
    CHESTPLATE(4),
    LEGGINGS(3),
    BOOTS(2),
    OFFHAND(1),
    HAND(0);

    /**
     * The equipment slot id.
     */
    private final int slot;

    /**
     * The equipment slot id for oldest versions. (<1.8)
     */
    private final int slotOld;

    /**
     * Creates a new equipment id identifier.
     *
     * @param slot the equipment id.
     */
    PluginEntityEquipmentSlot(int slot) {
        this.slot = slot;
        this.slotOld = slot == 0 ? 0 : slot - 1;
    }

    /**
     * Returns the id for this item slot.
     */
    public int getSlot() {
        return slot;
    }

    /**
     * Returns the old version id for this item slot.
     */
    public int getSlotOld() {
        return slotOld;
    }
}
