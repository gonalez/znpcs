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
     * The class start time.
     */
    private static final long START_TIME = System.currentTimeMillis();

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
    private Map<ZNConfigValue, Object> configValues;

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

        try {
            if (!path.toFile().exists())
                Files.createFile(path);

            this.load();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public void load() {
        // Set default configuration values
        configValues = Arrays.stream(ZNConfigValue.values()).filter(znConfigValue -> znConfigValue.getConfigType() == this.configType).collect(Collectors.toMap(key -> key, ZNConfigValue::getValue));

        try (BufferedReader reader = Files.newBufferedReader(path, CHARSET)) {
            JsonElement data = JSON_PARSER.parse(reader);
            if (data == null)
                return;

            for (ZNConfigValue znConfigValue : ZNConfigValue.values()) {
                if (znConfigValue.getConfigType() != configType)
                    continue;

                JsonElement jsonElement = configValues.size() == 1 ? data : (data.isJsonObject() ? data.getAsJsonObject().get(znConfigValue.name()) : null);
                if (jsonElement != null && !jsonElement.isJsonNull())
                    configValues.put(znConfigValue, ServersNPC.GSON.fromJson(jsonElement, $Gson$Types.newParameterizedTypeWithOwner(null, znConfigValue.getValue().getClass(), znConfigValue.getPrimitiveType())));
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }

        // Save to file
        save();
    }

    @Override
    public void save() {
        if (System.currentTimeMillis() - START_TIME < 1000 * 10)
            return;

        try (BufferedWriter writer = Files.newBufferedWriter(path, CHARSET)) {
            ServersNPC.GSON.toJson(configValues.size() == 1 ? configValues.values().iterator().next() : configValues, writer);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public void sendMessage(CommandSender player, ZNConfigValue znConfigValue) {
        player.sendMessage(Utils.color(getValue(znConfigValue)));
    }

    @Override
    public <T> T getValue(ZNConfigValue znConfigValue) {
        return (T) configValues.get(znConfigValue);
    }
}
