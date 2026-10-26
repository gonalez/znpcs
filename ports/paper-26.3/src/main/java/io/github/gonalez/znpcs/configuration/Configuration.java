/*
 * ZNPCs 5.3 compatibility port for Paper 26.3.
 * Modified from, or added to, ZNPCs by Gaston Gonzalez.
 * Licensed under the Apache License 2.0; see LICENSE and NOTICE.
 */
package io.github.gonalez.znpcs.configuration;

import com.google.common.collect.ImmutableList;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import io.github.gonalez.znpcs.ServersNPC;
import io.github.gonalez.znpcs.npc.NPCModel;
import io.github.gonalez.znpcs.utility.Utils;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import org.bukkit.command.CommandSender;

/** Keeps the 5.0 JSON schema; never saves defaults over a failed read. */
public class Configuration {
  static final String CONFIG_FORMAT = ".json";
  private final String name;
  private final Path path;
  private final Map<ConfigurationValue, Object> configurationValues = new LinkedHashMap<>();
  private JsonObject preserved = new JsonObject();
  private final Map<Integer, JsonObject> preservedNpcRecords = new HashMap<>();
  private boolean loaded;
  public static final Configuration CONFIGURATION = new Configuration("config");
  public static final Configuration MESSAGES = new Configuration("messages");
  public static final Configuration CONVERSATIONS = new Configuration("conversations");
  public static final Configuration DATA = new Configuration("data");
  public static final ImmutableList<Configuration> SAVE_CONFIGURATIONS =
      ImmutableList.of(CONVERSATIONS, DATA);

  protected Configuration(String name) {
    this(name, ServersNPC.PLUGIN_FOLDER.toPath().resolve(name + CONFIG_FORMAT));
  }

  private Configuration(String name, Path path) {
    this.name = name;
    this.path = path;
    for (ConfigurationValue v : ConfigurationValue.VALUES_BY_NAME.get(name))
      configurationValues.put(v, v.getValue());
    onLoad();
  }

  protected void onLoad() {
    synchronized (path) {
      try {
        Files.createDirectories(path.getParent());
        if (Files.exists(path)) {
          JsonElement data = JsonParser.parseString(Files.readString(path, StandardCharsets.UTF_8));
          boolean single = configurationValues.size() == 1;
          if (data == null
              || data.isJsonNull()
              || (!single && !data.isJsonObject())
              || (single && !data.isJsonArray()))
            throw new JsonParseException("Unexpected JSON structure");
          if (!single) preserved = data.getAsJsonObject().deepCopy();
          for (ConfigurationValue v : configurationValues.keySet()) {
            JsonElement value = single ? data : data.getAsJsonObject().get(v.name());
            if (value == null || value.isJsonNull()) continue;
            Type type =
                single
                    ? TypeToken.getParameterized(ArrayList.class, v.getPrimitiveType()).getType()
                    : v.getPrimitiveType();
            Object decoded = ServersNPC.GSON.fromJson(value, type);
            if (decoded == null) throw new JsonParseException("Invalid value for " + v);
            configurationValues.put(v, decoded);
          }
          if (name.equals("data")) {
            Set<Integer> ids = new HashSet<>();
            for (NPCModel npc : this.<List<NPCModel>>getValue(ConfigurationValue.NPC_LIST)) {
              if (npc == null
                  || npc.getNpcType() == null
                  || npc.getLocation() == null
                  || !ids.add(npc.getId()))
                throw new JsonParseException("Invalid NPC type, location or duplicate id");
            }
            for (JsonElement record : data.getAsJsonArray()) {
              JsonObject object = record.getAsJsonObject();
              preservedNpcRecords.put(object.get("id").getAsInt(), object.deepCopy());
            }
          }
        }
        loaded = true;
        if (!Files.exists(path)) save();
      } catch (IOException | RuntimeException e) {
        loaded = false;
        throw new IllegalStateException(
            "Cannot read " + path + "; original file has been left untouched", e);
      }
    }
  }

  public void save() {
    synchronized (path) {
      if (!loaded) return;
      Path temporary = null;
      try {
        JsonElement data;
        if (configurationValues.size() == 1)
          data = ServersNPC.GSON.toJsonTree(configurationValues.values().iterator().next());
        else {
          JsonObject object = preserved.deepCopy();
          configurationValues.forEach(
              (key, value) -> object.add(key.name(), ServersNPC.GSON.toJsonTree(value)));
          data = object;
        }
        if (name.equals("data")) {
          JsonArray records = new JsonArray();
          for (JsonElement record : data.getAsJsonArray()) {
            JsonObject current = record.getAsJsonObject();
            JsonObject original = preservedNpcRecords.get(current.get("id").getAsInt());
            JsonObject merged = original == null ? new JsonObject() : original.deepCopy();
            // Remove cleared known fields, while retaining fields this version cannot interpret.
            for (var field : NPCModel.class.getDeclaredFields()) {
              if (!java.lang.reflect.Modifier.isStatic(field.getModifiers()))
                merged.remove(field.getName());
            }
            current.entrySet().forEach(entry -> merged.add(entry.getKey(), entry.getValue()));
            records.add(merged);
          }
          data = records;
        }
        Files.createDirectories(path.getParent());
        temporary = Files.createTempFile(path.getParent(), path.getFileName().toString(), ".tmp");
        Files.writeString(temporary, ServersNPC.GSON.toJson(data), StandardCharsets.UTF_8);
        try {
          Files.move(
              temporary, path, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
        } catch (AtomicMoveNotSupportedException e) {
          Files.move(temporary, path, StandardCopyOption.REPLACE_EXISTING);
        }
      } catch (IOException e) {
        throw new IllegalStateException("Cannot safely save " + path, e);
      } finally {
        if (temporary != null)
          try {
            Files.deleteIfExists(temporary);
          } catch (IOException ignored) {
          }
      }
    }
  }

  @SuppressWarnings("unchecked")
  public <T> T getValue(ConfigurationValue v) {
    synchronized (path) {
      return (T) configurationValues.get(v);
    }
  }

  public void sendMessage(CommandSender sender, ConfigurationValue v, Object... args) {
    sender.sendMessage(Utils.toColor(String.format(this.<String>getValue(v), args)));
  }
}
