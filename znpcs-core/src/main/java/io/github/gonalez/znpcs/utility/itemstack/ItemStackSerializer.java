package io.github.gonalez.znpcs.utility.itemstack;

import com.google.gson.*;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Base64;

/**
 * @author Gaston Gonzalez {@literal <znetworkw.dev@gmail.com>}
 */
public class ItemStackSerializer implements JsonSerializer<ItemStack>, JsonDeserializer<ItemStack> {
    /**
     * Used when an itemstack cannot be deserialized.
     */
    private static final ItemStack DEFAULT = new ItemStack(Material.AIR);

    @Override
    public ItemStack deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(Base64.getDecoder().decode(json.getAsString()));
             BukkitObjectInputStream bukkitObjectOutputStream = new BukkitObjectInputStream(byteArrayInputStream)) {
            return (ItemStack) bukkitObjectOutputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            return DEFAULT;
        }
    }

    @Override
    public JsonElement serialize(ItemStack src, Type typeOfSrc, JsonSerializationContext context) {
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
             BukkitObjectOutputStream bukkitObjectOutputStream = new BukkitObjectOutputStream(byteArrayOutputStream)) {
            bukkitObjectOutputStream.writeObject(src);
            return new JsonPrimitive(Base64.getEncoder().encodeToString(byteArrayOutputStream.toByteArray()));
        } catch (IOException e) {
            throw new JsonParseException("can't serialize itemstack", e);
        }
    }
}
