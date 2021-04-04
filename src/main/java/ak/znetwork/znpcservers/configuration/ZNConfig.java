package ak.znetwork.znpcservers.configuration;

import ak.znetwork.znpcservers.ServersNPC;
import ak.znetwork.znpcservers.configuration.enums.ZNConfigValue;
import ak.znetwork.znpcservers.configuration.enums.type.ZNConfigType;
import ak.znetwork.znpcservers.configuration.impl.ZNConfigImpl;
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
public class ZNConfig implements ZNConfigImpl {

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
    private final ZNConfigType configType;

    /**
     * The configuration path.
     */
    private final Path path;

    /**
     * A map that contains the configuration values.
     */
    private final Map<ZNConfigValue, Object> configValues;

    /**
     * Creates a new configuration.
     *
     * @param znConfigType The configuration type.
     * @param path The configuration path.
     */
    public ZNConfig(ZNConfigType znConfigType,
                    Path path) {
        this.configType = znConfigType;
        this.path = path;
        this.configValues = Arrays.stream(ZNConfigValue.values()).filter(znConfigValue ->
                znConfigValue.getConfigType() == configType).
                collect(Collectors.toMap(key -> key, ZNConfigValue::getValue));
        load();
    }

    @Override
    public void load() {
        synchronized (path) {
            try (Reader reader = Files.newBufferedReader(path, CHARSET)) {
                JsonElement data = JSON_PARSER.parse(reader);
                if (data == null) {
                    // No data found
                    return;
                }

                for (ZNConfigValue configValue : configValues.keySet()) {
                    JsonElement jsonElement = configValues.size() == 1 ?
                            data : data.isJsonObject() ?
                            data.getAsJsonObject().get(configValue.name()) : null;
                    if (jsonElement != null && !jsonElement.isJsonNull()) {
                        configValues.put(configValue, ServersNPC.GSON.fromJson(jsonElement, $Gson$Types.newParameterizedTypeWithOwner(null, configValue.getValue().getClass(), configValue.getPrimitiveType())));
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

    @Override
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

    @Override
    public void sendMessage(CommandSender sender, ZNConfigValue znConfigValue) {
        sender.sendMessage(Utils.color(getValue(znConfigValue)));
    }

    @Override
    public <T> T getValue(ZNConfigValue znConfigValue) {
        return (T) configValues.get(znConfigValue);
    }
}
