package ak.znetwork.znpcservers.utility.inventory;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

/**
 * <p>Copyright (c) ZNetwork, 2020.</p>
 *
 * @author ZNetwork
 * @since 2/8/2021
 */
public class ZInventoryHolder implements InventoryHolder {
    /**
     * The inventory.
     */
    private final ZInventory zInventory;

    /**
     * Creates a new inventory holder for a {@link ZInventory}.
     *
     * @param zInventory The inventory.
     */
    public ZInventoryHolder(ZInventory zInventory) {
        this.zInventory = zInventory;
    }

    /**
     * Returns the holder inventory.
     *
     * @return Returns the holder inventory.
     */
    public ZInventory getzInventory() {
        return zInventory;
    }

    @Override
    public Inventory getInventory() {
        return zInventory.getInventory();
    }
}
