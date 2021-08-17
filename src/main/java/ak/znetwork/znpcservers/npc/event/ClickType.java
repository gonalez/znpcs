package ak.znetwork.znpcservers.npc.event;

/**
 * Used to determine the click type when interacting with a npc.
 */
public enum ClickType {
    /**
     * Represents when an npc is right-clicked.
     */
    RIGHT,
    /**
     * Represents when an npc is left-clicked.
     */
    LEFT,
    /**
     * Represents when cannot determine the click type.
     */
    DEFAULT;

    /**
     * Locates a click type for the given name.
     *
     * @param clickName The action name.
     * @return The corresponding enum.
     */
    public static ClickType forName(String clickName) {
        if (clickName.startsWith("INTERACT")) {
            return RIGHT;
        } else if (clickName.startsWith("ATTACK")) {
            return LEFT;
        } else {
            return DEFAULT;
        }
    }
}