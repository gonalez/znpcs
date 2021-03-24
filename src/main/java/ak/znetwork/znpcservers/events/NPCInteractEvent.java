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
     * Returns the player who interacted with the npc.
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * Returns the click type.
     */
    public ClickType getClickType() {
        return clickType;
    }

    /**
     * Returns the npc that was clicked.
     */
    public ZNPC getNpc() {
        return npc;
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
    public enum ClickType {

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
         * @param clickName The action name.
         * @return The corresponding enum.
         */
        public static ClickType forName(String clickName) {
            if (clickName.startsWith("INTERACT")) {
                return RIGHT;
            } else if (clickName.startsWith("ATTACK")) {
                return LEFT;
            } else {
                throw new IllegalArgumentException("Cannot find click type for " + clickName);
            }
        }
    }
}
