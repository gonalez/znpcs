package ak.znetwork.znpcservers.manager;

import ak.znetwork.znpcservers.ServersNPC;
import ak.znetwork.znpcservers.configuration.ZNConfig;
import ak.znetwork.znpcservers.configuration.enums.type.ZNConfigType;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableMap;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

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

    /**
     * The configuration names.
     */
    private static final Collection<String> configNames = Arrays.stream(ZNConfigType.values()).map(Enum::name).collect(Collectors.toList());

    static {
        final ImmutableMap.Builder<String, ZNConfig> builder = ImmutableMap.builder();

        for (String name : configNames) {
            builder.put(name, new ZNConfig(ZNConfigType.valueOf(name), ServersNPC.PLUGIN_FOLDER.toPath().resolve(String.format("%s.json", name.toLowerCase()))));
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
