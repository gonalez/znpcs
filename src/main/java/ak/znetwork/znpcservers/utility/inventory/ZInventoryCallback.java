package ak.znetwork.znpcservers.utility.inventory;

import org.bukkit.event.inventory.InventoryClickEvent;

/**
 * <p>Copyright (c) ZNetwork, 2020.</p>
 *
 * @author ZNetwork
 * @since 2/8/2021
 */
public interface ZInventoryCallback {
    /**
     * Called when a item in a inventory is clicked.
     *
     * @param event The item click event.
     */
    void onClick(InventoryClickEvent event);
}
