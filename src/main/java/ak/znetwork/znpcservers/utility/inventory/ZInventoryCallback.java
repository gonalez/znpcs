package ak.znetwork.znpcservers.utility.inventory;

import org.bukkit.event.inventory.InventoryClickEvent;

/**
 * Interface used when a player interacts with an item
 * as described in {@link ZInventoryItem}.
 */
public interface ZInventoryCallback {
    /**
     * Called when a {@link ZInventoryItem} is clicked.
     *
     * @param event The item click event.
     */
    void onClick(InventoryClickEvent event);
}
