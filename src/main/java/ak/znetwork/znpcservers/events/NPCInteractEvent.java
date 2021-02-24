package ak.znetwork.znpcservers.events;

import ak.znetwork.znpcservers.npc.ZNPC;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import lombok.Getter;

/**
 * <p>Copyright (c) ZNetwork, 2020.</p>
 *
 * @author ZNetwork
 * @since 07/02/2020
 */
@Getter
public class NPCInteractEvent extends Event {

    private static final HandlerList handlerList = new HandlerList();

    /**
     * The player who interacted with the npc.
     */
    private final Player player;

    /**
     * The npc that was interacted with.
     */
    private final ZNPC znpc;

    /**
     * @param player The player who clicked the npc.
     * @param npc    The npc that was clicked.
     */
    public NPCInteractEvent(Player player,
                            ZNPC npc) {
        this.player = player;
        this.znpc = npc;
    }

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }
}
