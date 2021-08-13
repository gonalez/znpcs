package ak.znetwork.znpcservers.utility.inventory;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public class ZInventoryHolder implements InventoryHolder {
    /** The inventory in which the holder will be created for. */
    private final ZInventory zInventory;

    /**
     * Creates a new {@link InventoryHolder} for a {@link ZInventory}.
     *
     * @param zInventory The inventory.
     */
    public ZInventoryHolder(ZInventory zInventory) {
        this.zInventory = zInventory;
    }

    /**
     * Returns the inventory.
     */
    public ZInventory getzInventory() {
        return zInventory;
    }

    @Override
    public Inventory getInventory() {
        return zInventory.getInventory();
    }
}
