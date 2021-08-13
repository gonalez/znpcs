package ak.znetwork.znpcservers.utility.itemstack;

import ak.znetwork.znpcservers.utility.Utils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Builder for a {@link ItemStack}.
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
     * @param stack The item stack.
     */
    protected ItemStackBuilder(ItemStack stack) {
        this.itemStack = stack;
        this.itemMeta = stack.getItemMeta();
    }

    /**
     * Creates a new {@link ItemStackBuilder} for the given material.
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
     * Sets the {@link ItemMeta#getDisplayName()} of this item stack.
     *
     * @param name The new item stack name.
     * @return The current instance.
     */
    public ItemStackBuilder setName(String name) {
        itemMeta.setDisplayName(Utils.toColor(name));
        return this;
    }

    /**
     * Sets the {@link ItemMeta#getLore()} of this item stack.
     *
     * @param lore The new item stack lore.
     * @return The current instance.
     */
    public ItemStackBuilder setLore(Iterable<String> lore) {
        itemMeta.setLore(StreamSupport.stream(lore.spliterator(), false)
                .map(Utils::toColor).collect(Collectors.toList()));
        itemStack.setItemMeta(itemMeta);
        return this;
    }

    /**
     * Sets the {@link ItemMeta#getLore()} of this item stack.
     *
     * @param lore The new item stack lore.
     * @return The current instance.
     */
    public ItemStackBuilder setLore(String... lore) {
        return setLore(Arrays.asList(lore));
    }

    /**
     * Sets the {@link ItemStack#getAmount()} of this item stack.
     *
     * @param amount The new item stack amount.
     * @return The current instance.
     */
    public ItemStackBuilder setAmount(int amount) {
        itemStack.setAmount(amount);
        return this;
    }

    /**
     * Builds the new {@link ItemStack}.
     *
     * @return A {@link ItemStack} with the current instance data.
     */
    public ItemStack build() {
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }
}
