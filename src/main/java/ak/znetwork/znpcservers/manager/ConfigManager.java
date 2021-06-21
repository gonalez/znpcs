package ak.znetwork.znpcservers.manager;

import ak.znetwork.znpcservers.ServersNPC;
import ak.znetwork.znpcservers.configuration.ZNConfig;
import ak.znetwork.znpcservers.configuration.enums.type.ZNConfigType;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableMap;

/**
 * <p>Copyright (c) ZNetwork, 2020.</p>
 *
 * @author ZNetwork
 * @since 07/02/2020
 */
public class ConfigManager {

    /**
     * The configuration types.
     */
    private static final ImmutableMap<String, ZNConfig> CONFIG_IMMUTABLE_MAP;

    static {
        final ImmutableMap.Builder<String, ZNConfig> builder = ImmutableMap.builder();
        for (ZNConfigType configType : ZNConfigType.values()) {
            String name = configType.name();
            builder.put(name, new ZNConfig(configType, ServersNPC.PLUGIN_FOLDER.toPath().resolve(String.format("%s.json", name.toLowerCase()))));
        }
        CONFIG_IMMUTABLE_MAP = builder.build();
    }

    /**
     * Returns the configuration for the given config type.
     *
     * @param type The configuration type.
     * @return The configuration.
     */
    public static ZNConfig getByType(ZNConfigType type) {
        return CONFIG_IMMUTABLE_MAP.get(type.name());
    }

    /**
     * Returns the configuration values.
     *
     * @return The configurations in the map.
     */
    public static ImmutableCollection<ZNConfig> all() {
        return CONFIG_IMMUTABLE_MAP.values();
    }
}
