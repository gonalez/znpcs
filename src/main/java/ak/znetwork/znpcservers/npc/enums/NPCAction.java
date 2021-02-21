package ak.znetwork.znpcservers.npc.enums;

/**
 * <p>Copyright (c) ZNetwork, 2020.</p>
 *
 * @author ZNetwork
 * @since 07/02/2020
 */
public enum NPCAction {

    /**
     * Represents an action executed by a player.
     */
    CMD,

    /**
     * Represents an action executed by the console.
     */
    CONSOLE,

    /**
     * Represents sending a message to a player.
     */
    MESSAGE,

    /**
     * Represents sending a player to another server (Bungee).
     */
    SERVER;

    /**
     * Gets NPCAction by name.
     *
     * @param text The action type name.
     * @return Corresponding enum or null if not found.
     */
    public static NPCAction fromString(String text) {
        for (NPCAction b : NPCAction.values()) {
            if (b.name().equalsIgnoreCase(text)) {
                return b;
            }
        }
        return null;
    }
}
