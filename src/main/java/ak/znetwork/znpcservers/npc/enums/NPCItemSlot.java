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
    HELMET(4),

    /**
     * Represents the chestplate of the npc.
     */
    CHEST_PLATE(3),

    /**
     * Represents the leggings of the npc.
     */
    LEGGINGS(2),

    /**
     * Represents the boots of the npc.
     */
    BOOTS(1),

    /**
     * Represents the hand of the npc.
     */
    HAND(0);

    /**
     * The equipment slot.
     */
    private final int equipmentSlot;

    /**
     * Creates a new equipment identifier.
     *
     * @param slot The equipment slot.
     */
    NPCItemSlot(int slot) {
        this.equipmentSlot = slot;
    }

    /**
     * Gets NPCItemSlot by name.
     *
     * @param text The item slot name.
     * @return Corresponding enum or null if not found.
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