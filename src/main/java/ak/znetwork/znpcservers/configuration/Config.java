package ak.znetwork.znpcservers.configuration;

import ak.znetwork.znpcservers.ServersNPC;
import ak.znetwork.znpcservers.utility.Utils;

import com.google.gson.JsonElement;
import com.google.gson.internal.$Gson$Types;
import com.google.gson.JsonParser;

import org.bukkit.command.CommandSender;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>Copyright (c) ZNetwork, 2020.</p>
 *
 * @author ZNetwork
 * @since 07/02/2020
 */
public class Config {
    /**
     * Creates a new parser.
     */
    private static final JsonParser JSON_PARSER = new JsonParser();

    /**
     * The charset.
     */
    private static final Charset CHARSET = StandardCharsets.UTF_8;

    /**
     * The configuration type.
     */
    private final ConfigType configType;

    /**
     * The configuration path.
     */
    private final Path path;

    /**
     * A map that contains the configuration values.
     */
    private final Map<ConfigValue, Object> configValues;

    /**
     * Creates a new configuration.
     *
     * @param configType The configuration type.
     * @param path The configuration path.
     */
    public Config(ConfigType configType,
                  Path path) {
        this.configType = configType;
        this.path = path;
        this.configValues = Arrays.stream(ConfigValue.values())
                .filter(znConfigValue -> znConfigValue.getConfigType() == configType)
                .collect(Collectors.toMap(key -> key, ConfigValue::getValue));
        load();
    }

    /**
     * Loads the configuration.
     */
    public void load() {
        synchronized (path) {
            try (Reader reader = Files.newBufferedReader(path, CHARSET)) {
                JsonElement data = JSON_PARSER.parse(reader);
                if (data == null) {
                    // No data found
                    return;
                }

                for (ConfigValue configValue : configValues.keySet()) {
                    final boolean single = configValues.size() == 1;
                    JsonElement jsonElement = single ?
                            data : data.isJsonObject() ?
                            data.getAsJsonObject().get(configValue.name()) : null;
                    if (jsonElement != null && !jsonElement.isJsonNull()) {
                        if (
                                !single
                                && configValue.getPrimitiveType().isEnum()) {
                            configValues.put(configValue, ServersNPC.GSON.fromJson(jsonElement, configValue.getPrimitiveType()));
                        } else {
                            configValues.put(configValue, ServersNPC.GSON.fromJson(jsonElement, $Gson$Types.newParameterizedTypeWithOwner(null, configValue.getValue().getClass(), configValue.getPrimitiveType())));
                        }
                    }
                }
            } catch (NoSuchFileException e) {
                // File not found, create the configuration with
                // The default provided values.
            } catch (IOException e) {
                throw new IllegalStateException("Failed to read config: " + configType.name());
            } finally {
                // Save configuration to file
                save();
            }
        }
    }

    /**
     * Saves configuration into config file.
     *
     * @throws IOException If configuration could not be saved.
     */
    public void save() {
        synchronized (path) {
            try (Writer writer = Files.newBufferedWriter(path, CHARSET)) {
                ServersNPC.GSON.toJson(configValues.size() == 1 ?
                        configValues.values().iterator().next() : configValues, writer);
            } catch (IOException e) {
                throw new IllegalStateException("Failed to save config: " + configType.name());
            }
        }
    }

    /**
     * Returns the configuration key value.
     *
     * @param configValue The configuration key.
     */
    public <T> T getValue(ConfigValue configValue) {
        synchronized (path) {
            return (T) configValues.get(configValue);
        }
    }

    /**
     * Sends configuration message to the given command sender.
     *
     * @param sender The sender to send the message for.
     * @param configValue The configuration message value.
     */
    public void sendMessage(CommandSender sender, ConfigValue configValue) {
        sender.sendMessage(Utils.toColor(getValue(configValue)));
    }
}
