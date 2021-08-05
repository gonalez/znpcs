package ak.znetwork.znpcservers.utility.itemstack;

import ak.znetwork.znpcservers.utility.Utils;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * <p>Copyright (c) ZNetwork, 2020.</p>
 *
 * @author ZNetwork
 * @since 3/8/2021
 */
public class ItemStackBuilder {
    /**
     * The {@link ItemStack}.
     */
    private final ItemStack itemStack;

    /**
     * The {@link ItemStack} data.
     */
    private final ItemMeta itemMeta;

    /**
     * Creates a new builder for a {@link ItemStack}.
     *
     * @param stack The {@link ItemStack}.
     */
    protected ItemStackBuilder(ItemStack stack) {
        this.itemStack = stack;
        this.itemMeta = stack.getItemMeta();
    }

    /**
     * Creates a new {@link ItemStack} builder for a material.
     *
     * @param material The material.
     * @return A {@link ItemStackBuilder} instance with the given material as a {@link ItemStack}.
     */
    public static ItemStackBuilder forMaterial(Material material) {
        if (material == null || material == Material.AIR) {
            throw new IllegalStateException("can't create builder for a NULL material.");
        }
        return new ItemStackBuilder(new ItemStack(material, 1));
    }

    /**
     * Sets the new {@link ItemStack} name.
     *
     * @param name The new {@link ItemStack} name.
     * @return The current instance.
     */
    public ItemStackBuilder setName(String name) {
        itemMeta.setDisplayName(Utils.toColor(name));
        return this;
    }

    /**
     * Sets the new {@link ItemStack} lore.
     *
     * @param lore The new {@link ItemStack} lore.
     * @return The current instance.
     */
    public ItemStackBuilder setLore(Iterable<String> lore) {
        itemMeta.setLore(StreamSupport.stream(lore.spliterator(), false)
                .map(Utils::toColor).collect(Collectors.toList()));
        itemStack.setItemMeta(itemMeta);
        return this;
    }

    /**
     * Sets the new {@link ItemStack} lore.
     *
     * @param lore The new {@link ItemStack} lore.
     * @return The current instance.
     */
    public ItemStackBuilder setLore(String... lore) {
        return setLore(Arrays.asList(lore));
    }

    /**
     * Sets the new {@link ItemStack} amount.
     *
     * @param amount The new {@link ItemStack} amount.
     * @return The current instance.
     */
    public ItemStackBuilder setAmount(int amount) {
        itemStack.setAmount(amount);
        return this;
    }

    /**
     * Builds & updates the new {@link ItemStack}.
     *
     * @return The {@link ItemStack} with the new data.
     */
    public ItemStack build() {
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }
}
