package ak.znetwork.znpcservers.utility.inventory;

import org.bukkit.inventory.ItemStack;

/**
 * <p>Copyright (c) ZNetwork, 2020.</p>
 *
 * @author ZNetwork
 * @since 2/8/2021
 */
public class ZInventoryItem {
    /**
     * The item.
     */
    private final ItemStack itemStack;

    /**
     * The item inventory position slot.
     */
    private final int slot;

    /**
     * The item callback.
     */
    private final ZInventoryCallback zInventoryCallback;

    /**
     * Creates a new inventory-item for a {@link ZInventoryPage}.
     *
     * @param itemStack The item-stack.
     * @param slot The item inventory position slot.
     * @param zInventoryCallback The item click callback.
     */
    public ZInventoryItem(ItemStack itemStack,
                          int slot,
                          ZInventoryCallback zInventoryCallback) {
        this.itemStack = itemStack;
        this.slot = slot;
        this.zInventoryCallback = zInventoryCallback;
    }

    /**
     * Returns the item stack.
     *
     * @return The item stack.
     */
    public ItemStack getItemStack() {
        return itemStack;
    }

    /**
     * Returns the item slot.
     *
     * @return The item slot position in inventory.
     */
    public int getSlot() {
        return slot;
    }

    /**
     * Returns the callback for when the item is clicked.
     *
     * @return The item callback for when the item is clicked.
     */
    public ZInventoryCallback getInventoryCallback() {
        return zInventoryCallback;
    }
}
