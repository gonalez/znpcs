package ak.znetwork.znpcservers.manager;

import ak.znetwork.znpcservers.ServersNPC;
import ak.znetwork.znpcservers.configuration.Config;
import ak.znetwork.znpcservers.configuration.ConfigKey;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableMap;

/**
 * @inheritDoc
 */
public final class ConfigManager {
    /**
     * The configuration types.
     */
    private static final ImmutableMap<ConfigKey, Config> CONFIG_IMMUTABLE_MAP;

    static {
        final ImmutableMap.Builder<ConfigKey, Config> builder = ImmutableMap.builder();
        for (ConfigKey configType : ConfigKey.values()) {
            builder.put(configType, new Config(configType, ServersNPC.PLUGIN_FOLDER.toPath().resolve(String.format("%s.json", configType.name().toLowerCase()))));
        }
        CONFIG_IMMUTABLE_MAP = builder.build();
    }

    /**
     * Returns the configuration for the given config type.
     *
     * @param type The configuration type.
     * @return The configuration.
     */
    public static Config getByType(ConfigKey type) {
        return CONFIG_IMMUTABLE_MAP.get(type);
    }

    /**
     * Returns the configuration values.
     *
     * @return The configurations in the map.
     */
    public static ImmutableCollection<Config> all() {
        return CONFIG_IMMUTABLE_MAP.values();
    }

    /** Default constructor */
    private ConfigManager() {
        throw new AssertionError("This class is not intended to be initialized.");
    }
}
