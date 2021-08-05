package ak.znetwork.znpcservers.listeners;

import ak.znetwork.znpcservers.ServersNPC;
import ak.znetwork.znpcservers.utility.inventory.ZInventory;
import ak.znetwork.znpcservers.utility.inventory.ZInventoryHolder;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

/**
 * <p>Copyright (c) ZNetwork, 2020.</p>
 *
 * @author ZNetwork
 * @since 2/8/2021
 */
public class InventoryListener implements Listener {
    /**
     * Creates and register the necessary events for a {@link ZInventory}.
     *
     * @param serversNPC The plugin instance.
     */
    public InventoryListener(ServersNPC serversNPC) {
        serversNPC.getServer().getPluginManager().registerEvents(this, serversNPC);
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        // check if the entity who clicked the inventory is a player
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }
        // check if player clicked a item
        if (event.getCurrentItem() == null) {
            return;
        }
        // check if the inventory is valid
        if (!(event.getInventory().getHolder() instanceof ZInventoryHolder)) {
            return;
        }
        // cancel event
        event.setCancelled(true);
        ZInventory zInventory = ((ZInventoryHolder) event.getInventory().getHolder()).getzInventory();
        // check if clicked item exists in the inventory
        if (!zInventory.getCurrentPage().hasItem(event.getRawSlot())) {
            return;
        }
        // handle item click event
        zInventory.getCurrentPage().findItem(event.getRawSlot()).getInventoryCallback().onClick(event);
        ((Player) event.getWhoClicked()).updateInventory();
    }
}
