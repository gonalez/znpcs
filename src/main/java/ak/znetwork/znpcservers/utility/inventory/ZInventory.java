package ak.znetwork.znpcservers.utility.inventory;

import ak.znetwork.znpcservers.utility.Utils;
import com.google.common.base.Preconditions;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

/**
 * <p>Copyright (c) ZNetwork, 2020.</p>
 *
 * @author ZNetwork
 * @since 2/8/2021
 */
public class ZInventory {
    /**
     * The player to create the inventory for.
     */
    private final Player player;

    /**
     * The current inventory page.
     */
    private ZInventoryPage currentPage;

    /**
     * The bukkit inventory.
     */
    private Inventory inventory;

    /**
     * Creates a new inventory for the given player.
     *
     * @param player The player to create the inventory for.
     */
    public ZInventory(Player player) {
        this.player = player;
    }

    /**
     * Returns the player to create the inventory for.
     *
     * @return The player to create the inventory for.
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * Returns the current inventory page.
     *
     * @return The current inventory page.
     */
    public ZInventoryPage getCurrentPage() {
        return currentPage;
    }

    /**
     * Returns the bukkit inventory.
     *
     * @return The bukkit inventory.
     */
    public Inventory getInventory() {
        return inventory;
    }

    /**
     * Sets the new current page for the inventory.
     *
     * @param currentPage The new current page.
     */
    public void setCurrentPage(ZInventoryPage currentPage) {
        this.currentPage = currentPage;
    }

    /**
     * Creates the inventory for the given page.
     *
     * @param zInventoryPage The page.
     * @return An new inventory instance.
     */
    public Inventory build(ZInventoryPage zInventoryPage) {
        Preconditions.checkNotNull(zInventoryPage);
        setCurrentPage(zInventoryPage);
        // clear old items
        zInventoryPage.getInventoryItems().removeIf(zInventoryItem -> !zInventoryItem.isDefault());
        // update the page with the new items
        zInventoryPage.update();
        inventory = Bukkit.createInventory(new ZInventoryHolder(this), zInventoryPage.getRows(), Utils.toColor(zInventoryPage.getInventoryName()));
        zInventoryPage.getInventoryItems().forEach(zInventoryItem -> inventory.setItem(zInventoryItem.getSlot(), zInventoryItem.getItemStack()));
        return inventory;
    }

    /**
     * Creates the inventory.
     *
     * @return An new inventory instance.
     */
    public Inventory build() {
        return build(currentPage);
    }
}
