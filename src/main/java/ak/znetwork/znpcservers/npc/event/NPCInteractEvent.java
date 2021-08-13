package ak.znetwork.znpcservers.npc.event;

import ak.znetwork.znpcservers.npc.event.type.ClickType;
import ak.znetwork.znpcservers.npc.NPC;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Used when a {@link ak.znetwork.znpcservers.user.ZUser} interacts with an {@link NPC}.
 */
public class NPCInteractEvent extends Event {
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
    private final NPC npc;

    /** ... */
    private static final HandlerList handlerList = new HandlerList();

    /**
     * Creates a new interact event for a {@link NPC}.
     *
     * @param player The player who clicked the npc.
     * @param clickType The click type.
     * @param npc The npc that was clicked.
     */
    public NPCInteractEvent(Player player,
                            ClickType clickType,
                            NPC npc) {
        this.player = player;
        this.clickType = clickType;
        this.npc = npc;
    }

    /**
     * Creates a new interact event for a npc.
     *
     * @param player The player who clicked the npc.
     * @param clickType The click type.
     * @param npc The npc that was clicked.
     */
    public NPCInteractEvent(Player player,
                            String clickType,
                            NPC npc) {
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
    public NPC getNpc() {
        return npc;
    }

    /**
     * Returns {@code true} if the npc was interacted with right click.
     *
     * @return If the npc was interacted with right click.
     */
    public boolean isRightClick() {
        return clickType == ClickType.RIGHT;
    }

    /**
     * Returns {@code true} if the npc was interacted with left click.
     *
     * @return If the npc was interacted with left click.
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
}
