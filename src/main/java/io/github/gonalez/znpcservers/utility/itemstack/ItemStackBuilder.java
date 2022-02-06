package io.github.gonalez.znpcservers.utility.itemstack;

import io.github.gonalez.znpcservers.utility.Utils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Builder for an {@link ItemStack}.
 *
 * @author Gaston Gonzalez {@literal <znetworkw.dev@gmail.com>}
 */
public class ItemStackBuilder {
    /**
     * The {@link ItemStack}.
     */
    private final ItemStack itemStack;

    /**
     * The {@link ItemStack#getItemMeta()} of the object {@code itemStack}.
     */
    private final ItemMeta itemMeta;

    /**
     * Creates a {@link ItemStackBuilder} for the given {@link ItemStack}.
     *
     * @param stack the bukkit item stack.
     */
    protected ItemStackBuilder(ItemStack stack) {
        this.itemStack = stack;
        this.itemMeta = stack.getItemMeta();
    }

    /**
     * Creates a new {@link ItemStackBuilder} for the given material.
     *
     * @param material the material.
     * @return A new {@link ItemStackBuilder}.
     */
    public static ItemStackBuilder forMaterial(Material material) {
        if (material == null || material == Material.AIR) {
            throw new IllegalStateException("can't create builder for a NULL material.");
        }
        return new ItemStackBuilder(new ItemStack(material, 1));
    }

    /**
     * Sets the {@link ItemMeta#getDisplayName()} of this item stack.
     *
     * @param name the new item stack name.
     * @return this.
     */
    public ItemStackBuilder setName(String name) {
        itemMeta.setDisplayName(Utils.toColor(name));
        return this;
    }

    /**
     * Sets the {@link ItemMeta#getLore()} of this item stack.
     *
     * @param lore the new item stack lore.
     * @return this.
     */
    public ItemStackBuilder setLore(Iterable<String> lore) {
        itemMeta.setLore(StreamSupport.stream(lore.spliterator(), false)
            .map(Utils::toColor)
            .collect(Collectors.toList()));
        itemStack.setItemMeta(itemMeta);
        return this;
    }

    /**
     * Sets the {@link ItemMeta#getLore()} of this item stack.
     *
     * @param lore the new item stack lore.
     * @return this.
     */
    public ItemStackBuilder setLore(String... lore) {
        return setLore(Arrays.asList(lore));
    }

    /**
     * Sets the {@link ItemStack#getAmount()} of this item stack.
     *
     * @param amount the new item stack amount.
     * @return this.
     */
    public ItemStackBuilder setAmount(int amount) {
        itemStack.setAmount(amount);
        return this;
    }

    /**
     * Builds the new {@link ItemStack bukkit itemstack}.
     *
     * @return A {@link ItemStack} with the current instance data.
     */
    public ItemStack build() {
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }
}
