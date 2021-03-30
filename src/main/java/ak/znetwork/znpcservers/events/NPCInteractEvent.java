package ak.znetwork.znpcservers.events;

import ak.znetwork.znpcservers.npc.ZNPC;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * <p>Copyright (c) ZNetwork, 2020.</p>
 *
 * @author ZNetwork
 * @since 07/02/2020
 */
public class NPCInteractEvent extends Event {

    private static final HandlerList handlerList = new HandlerList();

    /**
     * The player who interacted with the npc.
     */
    private final Player player;

    /**
     * The click type.
     */
    private final ClickType clickType;

    /**
     * The npc that was interacted with.
     */
    private final ZNPC npc;

    /**
     * Creates a new interact event for a npc.
     *
     * @param player    The player who clicked the npc.
     * @param clickType The click type.
     * @param npc       The npc that was clicked.
     */
    public NPCInteractEvent(Player player,
                            ClickType clickType,
                            ZNPC npc) {
        this.player = player;
        this.clickType = clickType;
        this.npc = npc;
    }

    /**
     * Creates a new interact event for a npc.
     *
     * @param player    The player who clicked the npc.
     * @param clickType The click type.
     * @param npc       The npc that was clicked.
     */
    public NPCInteractEvent(Player player,
                            String clickType,
                            ZNPC npc) {
        this(player, ClickType.forName(clickType), npc);
    }

    /**
     * Returns the player who interacted with the npc.
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * Returns the npc that was clicked.
     */
    public ZNPC getNpc() {
        return npc;
    }

    /**
     * Returns if the npc was interacted with right click
     *
     * @return {@code true} If if the npc was interacted with right click
     */
    public boolean isRightClick() {
        return clickType == ClickType.RIGHT;
    }

    /**
     * Returns if the npc was interacted with left click
     *
     * @return {@code true} If if the npc was interacted with left click
     */
    public boolean isLeftClick() {
        return clickType == ClickType.LEFT;
    }

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }

    /**
     * Determines the click type when an npc is interacted.
     */
    enum ClickType {

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
                throw new IllegalArgumentException("Cannot find click type for " + actionName);
            }
        }
    }
}
