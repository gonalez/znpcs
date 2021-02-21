package ak.znetwork.znpcservers.configuration;

import ak.znetwork.znpcservers.configuration.enums.ZNConfigValue;
import ak.znetwork.znpcservers.configuration.enums.type.ZNConfigType;
import ak.znetwork.znpcservers.configuration.impl.ZNConfigImpl;
import ak.znetwork.znpcservers.utility.Utils;
import org.bukkit.command.CommandSender;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.EnumMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>Copyright (c) ZNetwork, 2020.</p>
 *
 * @author ZNetwork
 * @since 07/02/2020
 */
public final class ZNConfig implements ZNConfigImpl {

    /**
     * The yaml instance.
     */
    private static final Yaml yaml;

    // Creates Yaml instance.
    static {
        DumperOptions options = new DumperOptions();
        options.setPrettyFlow(true);
        yaml = new Yaml(options);
    }

    /**
     * The configuration type.
     */
    private final ZNConfigType znConfigType;

    /**
     * The configuration path.
     */
    private final Path path;

    /**
     * A map that contains the configuration values.
     */
    private final EnumMap<ZNConfigValue, Object> configValueStringEnumMap;

    /**
     * Creates a new configuration.
     *
     * @param znConfigType The configuration type.
     * @param path The configuration path.
     */
    public ZNConfig(ZNConfigType znConfigType,
                    Path path) {
        this.znConfigType = znConfigType;
        this.path = path;

        this.configValueStringEnumMap = new EnumMap<>(ZNConfigValue.class);

        try {
            if (!path.toFile().exists()) Files.createFile(path);

            this.load();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public void load() {
        this.configValueStringEnumMap.clear();

        try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            Map<String, Object> data = yaml.load(reader);

            if (data != null && !data.isEmpty()) {
                for (Map.Entry<String, Object> entry : data.entrySet()) {
                    if (entry.getKey() == null || entry.getKey().isEmpty()) continue;

                    ZNConfigValue znConfigValue = ZNConfigValue.valueOf(entry.getKey());
                    if (!entry.getValue().getClass().isAssignableFrom(znConfigValue.getPrimitiveType())) continue;

                    configValueStringEnumMap.put(znConfigValue, entry.getValue());
                }
            }

            // Default values.
            for (ZNConfigValue znConfigValue : ZNConfigValue.values())
                if (!configValueStringEnumMap.containsKey(znConfigValue) && znConfigValue.getConfigType() == znConfigType)
                    configValueStringEnumMap.put(znConfigValue, znConfigValue.getValue());

            // Save to file.
            save(configValueStringEnumMap.entrySet().stream().collect(Collectors.toMap(key -> key.getKey().name(), Map.Entry::getValue)));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public void save(Map<Object, Object> hashMap) {
        try (FileWriter writer = new FileWriter(new File(path.toUri()))) {
            yaml.dump(hashMap, writer);
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
        return (T) configValueStringEnumMap.get(znConfigValue);
    }
}
