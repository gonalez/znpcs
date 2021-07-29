package ak.znetwork.znpcservers.npc;

/**
 * Determines a npc equipment place by the slot id.
 *
 * <p>Copyright (c) ZNetwork, 2020.</p>
 *
 * @author ZNetwork
 * @since 07/02/2020
 */
public enum ZNPCSlot {
    /**
     * Represents the helmet of the npc.
     */
    HELMET(4, 5),

    /**
     * Represents the chest_plate of the npc.
     */
    CHESTPLATE(3, 4),

    /**
     * Represents the leggings of the npc.
     */
    LEGGINGS(2, 3),

    /**
     * Represents the boots of the npc.
     */
    BOOTS(1, 2),

    /**
     * Represents the off hand of the npc.
     */
    OFFHAND(0, 1),

    /**
     * Represents the main hand of the npc.
     */
    HAND(0, 0);

    /**
     * The equipment slot id for oldest versions.
     */
    private final int slotOld;

    /**
     * The equipment slot id for newer versions.
     */
    private final int slotNew;

    /**
     * Creates a new equipment identifier.
     *
     * @param slotOld The equipment id for oldest versions.
     * @param slotNew The equipment id for newer versions.
     */
    ZNPCSlot(int slotOld,
             int slotNew) {
        this.slotOld = slotOld;
        this.slotNew = slotNew;
    }

    /**
     * Returns the equipment id for newer versions.
     *
     * @return The equipment id for newer versions.
     */
    public int getSlotNew() {
        return slotNew;
    }

    /**
     * Returns The equipment id for oldest versions.
     *
     * @return The equipment id for oldest versions.
     */
    public int getSlotOld() {
        return slotOld;
    }
}