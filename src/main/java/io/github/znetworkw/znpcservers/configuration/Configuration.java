package io.github.znetworkw.znpcservers.configuration;

import io.github.znetworkw.znpcservers.ServersNPC;
import io.github.znetworkw.znpcservers.utility.Utils;
import com.google.common.collect.ImmutableList;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.internal.$Gson$Types;
import org.bukkit.command.CommandSender;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Represents a configuration.
 */
public class Configuration {
    /** The configuration format. */
    static final String CONFIG_FORMAT = ".json";

    /**
     * Creates a new parser.
     */
    private static final JsonParser JSON_PARSER = new JsonParser();

    /**
     * The configuration charset.
     */
    private static final Charset CHARSET = StandardCharsets.UTF_8;

    /**
     * The configuration name.
     */
    private final String name;

    /**
     * The configuration path.
     */
    private final Path path;

    /**
     * A map that contains the configuration values.
     */
    private final Map<ConfigurationValue, Object> configurationValues;

    public static final Configuration CONFIGURATION = new Configuration("config");
    public static final Configuration MESSAGES = new Configuration("messages");
    public static final Configuration CONVERSATIONS = new Configuration("conversations");
    public static final Configuration DATA = new Configuration("data");

    /** List of configurations that need to be saved. */
    public static final ImmutableList<Configuration> SAVE_CONFIGURATIONS = ImmutableList.of(CONVERSATIONS, DATA);

    /**
     * Creates a new {@link Configuration}.
     *
     * @param name The configuration name.
     */
    protected Configuration(String name) {
        this(name, ServersNPC.PLUGIN_FOLDER.toPath().resolve(name + CONFIG_FORMAT));
    }

    /**
     * Creates a new {@link Configuration}.
     *
     * @param name The configuration name.
     * @param path The configuration path.
     */
    private Configuration(String name,
                          Path path) {
        if (!path.getFileName().toString().endsWith(CONFIG_FORMAT)) {
            throw new IllegalStateException("invalid configuration format for: " + path.getFileName());
        }
        this.name = name;
        this.path = path;
        configurationValues = ConfigurationValue.VALUES_BY_NAME.get(name)
                .stream()
                .collect(Collectors.toMap(c -> c, ConfigurationValue::getValue));
        onLoad();
    }

    /**
     * Loads the configuration. Called when creating a new {@link Configuration}.
     */
    protected void onLoad() {
        synchronized (path) {
            try (Reader reader = Files.newBufferedReader(path, CHARSET)) {
                JsonElement data = JSON_PARSER.parse(reader);
                if (data == null) {
                    return;
                }
                for (ConfigurationValue configValue : configurationValues.keySet()) {
                    boolean single = configurationValues.size() == 1;
                    JsonElement jsonElement = single ?
                            data : data.isJsonObject() ?
                            data.getAsJsonObject().get(configValue.name()) : null;
                    if (jsonElement != null && !jsonElement.isJsonNull()) {
                        if (!single && configValue.getPrimitiveType().isEnum()) {
                            configurationValues.put(configValue, ServersNPC.GSON.fromJson(jsonElement, configValue.getPrimitiveType()));
                        } else {
                            configurationValues.put(configValue, ServersNPC.GSON.fromJson(jsonElement,
                                    $Gson$Types.newParameterizedTypeWithOwner(null, configValue.getValue().getClass(), configValue.getPrimitiveType())));
                        }
                    }
                }
            } catch (NoSuchFileException e) {
                // file not found, create the configuration with
                // the default provided values.
            } catch (IOException e) {
                throw new IllegalStateException("Failed to read config: " + name);
            } finally {
                save(); // save configuration to file
            }
        }
    }

    /**
     * Writes the configuration values into the file.
     */
    public void save() {
        synchronized (path) {
            try (Writer writer = Files.newBufferedWriter(path, CHARSET)) {
                ServersNPC.GSON.toJson(configurationValues.size() == 1 ?
                        configurationValues.values().iterator().next() : configurationValues, writer);
            } catch (IOException e) {
                throw new IllegalStateException("Failed to save config: " + name);
            }
        }
    }

    /**
     * Returns the configuration key value.
     */
    public <T> T getValue(ConfigurationValue configValue) {
        synchronized (path) {
            return (T) configurationValues.get(configValue);
        }
    }

    /**
     * Sends a configuration message to the given command sender.
     *
     * @param sender      The sender to send the message for.
     * @param configValue The configuration message value.
     */
    public void sendMessage(CommandSender sender, ConfigurationValue configValue, Object... replaces) {
        sender.sendMessage(Utils.toColor(String.format(getValue(configValue), replaces)));
    }
}
