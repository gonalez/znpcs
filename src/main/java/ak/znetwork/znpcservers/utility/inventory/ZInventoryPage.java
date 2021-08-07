package ak.znetwork.znpcservers.utility.inventory;

import ak.znetwork.znpcservers.utility.itemstack.ItemStackBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>Copyright (c) ZNetwork, 2020.</p>
 *
 * @author ZNetwork
 * @since 2/8/2021
 */
public abstract class ZInventoryPage {
    /**
     * The inventory.
     */
    private final ZInventory zInventory;

    /**
     * The page inventory name.
     */
    private final String inventoryName;

    /**
     * The page inventory rows.
     */
    private final int rows;

    /**
     * The page items.
     */
    private final List<ZInventoryItem> inventoryItems;

    /**
     * Creates a new page for a {@link ZInventory}.
     *
     * @param zInventory The inventory.
     * @param inventoryName The page inventory name;
     * @param rows The page rows.
     */
    public ZInventoryPage(ZInventory zInventory,
                          String inventoryName,
                          int rows) {
        this.zInventory = zInventory;
        this.inventoryName = inventoryName;
        this.rows = 9*rows;
        this.inventoryItems = new ArrayList<>();
        // check if the inventory opened a page before or its the first page
        if (zInventory.getInventory() != null) {
            // the last page
            final ZInventoryPage zInventoryPage = zInventory.getCurrentPage();
            // add back button
            addItem(ItemStackBuilder.forMaterial(Material.ARROW)
                    .setName(ChatColor.GREEN + "Go back")
                    .setLore(ChatColor.GRAY  + "click here...")
                    .build(), this.rows-9, true, event -> {
                zInventory.setCurrentPage(zInventoryPage);
                openInventory();
            });
        }
        // set new page for inventory
        zInventory.setCurrentPage(this);
    }

    /**
     * The inventory.
     *
     * @return Returns the inventory.
     */
    public ZInventory getInventory() {
        return zInventory;
    }

    /**
     * Returns the page inventory name.
     *
     * @return The page inventory name.
     */
    public String getInventoryName() {
        return inventoryName;
    }

    /**
     * Returns the page rows.
     *
     * @return The page rows.
     */
    public int getRows() {
        return rows;
    }

    /**
     * Returns the page items.
     *
     * @return The page items.
     */
    public List<ZInventoryItem> getInventoryItems() {
        return inventoryItems;
    }

    /**
     * Returns {@code true} if the page contains a item in the given slot.
     *
     * @param slot The slot to check for a item.
     * @return If the page contains a item in the given slot.
     */
    public boolean hasItem(int slot) {
        return inventoryItems.stream().anyMatch(zInventoryItem -> zInventoryItem.getSlot() == slot);
    }

    /**
     * Finds a item in the given slot, if no item is found
     * The method will throw an {@link IllegalStateException}
     *
     * @param slot The slot to find the item.
     * @return The found item.
     */
    public ZInventoryItem findItem(int slot) {
        return inventoryItems.stream()
                .filter(zInventoryItem -> zInventoryItem.getSlot() == slot)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("can't find item for slot " + slot));
    }

    /**
     * Adds a new item.
     *
     * @param itemStack The item-stack.
     * @param row The item position slot.
     * @param zInventoryCallback The item click callback.
     */
    public void addItem(ItemStack itemStack,
                        int row,
                        boolean isDefault,
                        ZInventoryCallback zInventoryCallback) {
        inventoryItems.add(new ZInventoryItem(itemStack, row, isDefault, zInventoryCallback));
    }

    /**
     * Adds a new item.
     *
     * @param itemStack The item-stack.
     * @param row The item position slot.
     * @param zInventoryCallback The item click callback.
     */
    public void addItem(ItemStack itemStack,
                        int row,
                        ZInventoryCallback zInventoryCallback) {
        addItem(itemStack, row, false, zInventoryCallback);
    }


    /**
     * The player that created the inventory.
     *
     * @return The player that created the inventory.
     */
    public Player getPlayer() {
        return zInventory.getPlayer();
    }

    /**
     * Open the inventory again for the player.
     */
    public void openInventory() {
        zInventory.getPlayer().openInventory(zInventory.build());
    }

    /**
     * Updates the inventory items.
     */
    public abstract void update();
}
