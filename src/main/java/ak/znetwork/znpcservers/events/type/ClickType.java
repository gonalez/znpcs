package ak.znetwork.znpcservers.events.type;

/**
 * Determines the click type when an npc is interacted.

 * <p>Copyright (c) ZNetwork, 2020.</p>
 *
 * @author ZNetwork
 * @since 07/02/2020
 */
public enum ClickType {
    /**
     * Represents when an npc is interacted and the click type cannot be determined.
     */
    DEFAULT,

    /**
     * Represents when an npc is right-clicked.
     */
    RIGHT,

    /**
     * Represents when an npc is left-clicked.
     */
    LEFT;

    /**
     * Locates a click type for the given action name.
     *
     * @param actionName The action name.
     * @return The corresponding enum.
     */
    public static ClickType forName(String actionName) {
        if (actionName.startsWith("INTERACT")) {
            return RIGHT;
        } else if (actionName.startsWith("ATTACK")) {
            return LEFT;
        } else {
            return DEFAULT;
        }
    }
}