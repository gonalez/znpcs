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

    private final Player player;

    private final ZNPC znpc;

    public NPCInteractEvent(Player player, ZNPC znpc) {
        this.player = player;
        this.znpc = znpc;
    }

    public Player getPlayer() {
        return player;
    }

    public ZNPC getNPC() {
        return znpc;
    }

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }
}
