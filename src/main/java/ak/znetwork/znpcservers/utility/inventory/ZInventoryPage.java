package ak.znetwork.znpcservers.utility.inventory;

import ak.znetwork.znpcservers.utility.itemstack.ItemStackBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
* An abstract class, necessary to build a {@link ZInventory}.
 */
public abstract class ZInventoryPage {
    /** The inventory in which the page will be created. */
    private final ZInventory zInventory;

    /** The name of the inventory of this page. */
    private final String pageName;

    /**
     * Represents the inventory {@link Inventory#getSize()} of this page.
     * <p>
     * Each row represents 9 slots for a inventory.
     * <b>NOTE:</b> An inventory cannot have a size of more than 54 slots
     * so the maximum of rows for an inventory is {@code 6}.
     * If try to create an page with more than 6 rows an exception will be thrown
     * as described in {@link ZInventory#build(ZInventoryPage)}
     */
    private final int rows;

    /**
     * The items for the page.
     */
    private final List<ZInventoryItem> inventoryItems;

    /**
     * Creates a new page for an {@link ZInventory}.
     *
     * @param zInventory    The inventory.
     * @param inventoryName The page name;
     * @param rows          The page rows.
     */
    public ZInventoryPage(ZInventory zInventory,
                          String inventoryName,
                          int rows) {
        this.zInventory = zInventory;
        this.pageName = inventoryName;
        this.rows = 9 * rows;
        this.inventoryItems = new ArrayList<>();
        if (zInventory.getInventory() != null) { // check if the inventory opened a page before
            final ZInventoryPage zInventoryPage = zInventory.getPage();
            // back button
            addItem(ItemStackBuilder.forMaterial(Material.ARROW)
                    .setName(ChatColor.GREEN + "Go back")
                    .setLore(ChatColor.GRAY  +  "click here...")
                    .build(), this.rows - 9, true, event -> {
                zInventory.setCurrentPage(zInventoryPage);
                openInventory();
            });
        }
        zInventory.setCurrentPage(this);
    }

    /**
     * Returns the page inventory.
     */
    public ZInventory getInventory() {
        return zInventory;
    }

    /**
     * Returns the page name.
     */
    public String getPageName() {
        return pageName;
    }

    /**
     * Returns the page rows.
     */
    public int getRows() {
        return rows;
    }

    /**
     * Returns the page items.
     */
    public List<ZInventoryItem> getInventoryItems() {
        return inventoryItems;
    }

    /**
     * Returns {@code true} if the page contains an item in the given slot.
     *
     * @param slot The slot to check for.
     * @return {@code true}  If the page contains an item in the given slot.
     */
    public boolean containsItem(int slot) {
        return inventoryItems.stream().anyMatch(zInventoryItem -> zInventoryItem.getSlot() == slot);
    }

    /**
     * Tries to find a item on the given slot.
     *
     * @param slot The slot.
     * @return The item.
     * @throws IllegalStateException If the item can't be found.
     */
    public ZInventoryItem findItem(int slot) {
        return inventoryItems.stream()
                .filter(zInventoryItem -> zInventoryItem.getSlot() == slot)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("can't find item for slot " + slot));
    }

    /**
     * Adds a new page item.
     *
     * @param itemStack The item stack.
     * @param slot      The item position slot.
     * @param callback  The item click callback.
     */
    public void addItem(ItemStack itemStack,
                        int slot,
                        boolean isDefault,
                        ZInventoryCallback callback) {
        inventoryItems.add(new ZInventoryItem(itemStack, slot, isDefault, callback));
    }

    /**
     * Adds a new page item.
     *
     * @param itemStack The item-stack.
     * @param slot      The item position slot.
     * @param callback  The item click callback.
     */
    public void addItem(ItemStack itemStack,
                        int slot,
                        ZInventoryCallback callback) {
        addItem(itemStack, slot, false, callback);
    }

    /**
     * Returns the player that created the inventory.
     */
    public Player getPlayer() {
        return zInventory.getPlayer();
    }

    /**
     * Open inventory again for the player.
     */
    public void openInventory() {
        zInventory.getPlayer().openInventory(zInventory.build());
    }

    /**
     * Updates the inventory items.
     */
    public abstract void update();
}
