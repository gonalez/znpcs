package ak.znetwork.znpcservers.utility.inventory;

import ak.znetwork.znpcservers.utility.Utils;
import com.google.common.base.Preconditions;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

/**
 * Represents an inventory.
 *
 * <p>
 * To build a inventory, extend {@link ZInventoryPage} and use the
 * {@link #setCurrentPage(ZInventoryPage)} method.
 *
 * @see ZInventoryPage
 */
public class ZInventory {
    private static final int MAX_ROWS = 6;

    /** The player that created the inventory. */
    private final Player player;

    /** The current page for inventory. */
    private ZInventoryPage page;

    /** The bukkit inventory. */
    private Inventory inventory;

    /**
     * Creates a {@link ZInventory} for the given player.
     *
     * @param player The player to create the inventory for.
     */
    public ZInventory(Player player) {
        this.player = player;
    }

    /**
     * Returns the player that created the inventory.
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * Returns the current page.
     */
    public ZInventoryPage getPage() {
        return page;
    }

    /**
     * Returns the bukkit inventory.
     */
    public Inventory getInventory() {
        return inventory;
    }

    /**
     * Sets the {@link #getPage()} of this inventory.
     *
     * @param page The new page.
     */
    public void setCurrentPage(ZInventoryPage page) {
        this.page = page;
    }

    /**
     * Builds the inventory for the given page.
     * If the page is not provided, the last page will be used.
     * <p>This method should be called after adding the items for the given page.</p>
     * If only want to update the inventory, use {@link #build()} instead.
     * @param page The page to create the inventory for.
     * @return A {@link Inventory} with the given page information.
     * @throws IllegalStateException If the given page is {@code null}.
     * @throws IllegalArgumentException If the page rows are greater than {@link #MAX_ROWS}.
     */
    public Inventory build(ZInventoryPage page) {
        if (page == null) {
            throw new IllegalStateException("page is null");
        }
        if (page.getRows() > MAX_ROWS) {
            throw new IllegalArgumentException(String.format("Unexpected rows size. Has %d, max %d",
                    page.getRows(), MAX_ROWS));
        }
        setCurrentPage(page);
        // clear old items
        page.getInventoryItems().removeIf(zInventoryItem -> !zInventoryItem.isDefault());
        page.update();
        inventory = Bukkit.createInventory(new ZInventoryHolder(this), page.getRows(), Utils.toColor(page.getPageName()));
        page.getInventoryItems().forEach(zInventoryItem -> inventory.setItem(zInventoryItem.getSlot(), zInventoryItem.getItemStack()));
        return inventory;
    }

    /**
     * Builds the inventory for the last page.
     * @see #build(ZInventoryPage)
     */
    public Inventory build() {
        return build(page);
    }
}
