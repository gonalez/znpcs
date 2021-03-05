package ak.znetwork.znpcservers.npc.enums;

import lombok.Getter;

/**
 * Determines a npc equipment place by the slot id.
 *
 * <p>Copyright (c) ZNetwork, 2020.</p>
 *
 * @author ZNetwork
 * @since 07/02/2020
 */
@Getter
public enum NPCItemSlot {

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
    NPCItemSlot(int slotOld,
                int slotNew) {
        this.slotOld = slotOld;
        this.slotNew = slotNew;
    }

    /**
     * Gets NPCItemSlot by name.
     *
     * @param text The item slot name.
     * @return     The corresponding enum or {@code null} if not found.
     */
    public static NPCItemSlot fromString(String text) {
        for (NPCItemSlot b : NPCItemSlot.values()) {
            if (b.name().equalsIgnoreCase(text)) {
                return b;
            }
        }
        return null;
    }
}