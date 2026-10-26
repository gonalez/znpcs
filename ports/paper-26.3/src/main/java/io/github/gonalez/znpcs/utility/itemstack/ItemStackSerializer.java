/*
 * ZNPCs 5.3 compatibility port for Paper 26.3.
 * Modified from, or added to, ZNPCs by Gaston Gonzalez.
 * Licensed under the Apache License 2.0; see LICENSE and NOTICE.
 */
package io.github.gonalez.znpcs.utility.itemstack;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.util.Base64;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

public class ItemStackSerializer implements JsonSerializer<ItemStack>, JsonDeserializer<ItemStack> {
  private static final ItemStack DEFAULT = new ItemStack(Material.AIR);

  /*
   * Enabled aggressive exception aggregation
   */
  private ItemStack deserializeItemStack(byte[] data) {
    try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(data); ) {
      ItemStack itemStack;
      try (BukkitObjectInputStream bukkitObjectInputStream =
          new BukkitObjectInputStream((InputStream) byteArrayInputStream); ) {
        itemStack = (ItemStack) bukkitObjectInputStream.readObject();
      }
      return itemStack;
    } catch (IOException | ClassNotFoundException e) {
      throw new JsonParseException(
          "Cannot read a stored equipment item; refusing to replace it with air", e);
    }
  }

  public ItemStack deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
      throws JsonParseException {
    String base64String = json.getAsString();
    byte[] data = Base64.getDecoder().decode(base64String);
    return this.deserializeItemStack(data);
  }

  /*
   * Enabled aggressive exception aggregation
   */
  private String serializeItemStack(ItemStack itemStack) {
    try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(); ) {
      String string;
      try (BukkitObjectOutputStream bukkitObjectOutputStream =
          new BukkitObjectOutputStream((OutputStream) byteArrayOutputStream); ) {
        bukkitObjectOutputStream.writeObject((Object) itemStack);
        string = Base64.getEncoder().encodeToString(byteArrayOutputStream.toByteArray());
      }
      return string;
    } catch (IOException e) {
      throw new JsonParseException("Cannot serialize itemstack", (Throwable) e);
    }
  }

  public JsonElement serialize(ItemStack src, Type typeOfSrc, JsonSerializationContext context) {
    String serialized = this.serializeItemStack(src);
    return new JsonPrimitive(serialized);
  }
}
