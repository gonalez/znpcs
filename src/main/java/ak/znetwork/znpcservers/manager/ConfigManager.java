/*
 *
 * ZNServersNPC
 * Copyright (C) 2019 Gaston Gonzalez (ZNetwork)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 *
 */
package ak.znetwork.znpcservers.manager;

import ak.znetwork.znpcservers.ServersNPC;
import ak.znetwork.znpcservers.configuration.ZNConfig;
import ak.znetwork.znpcservers.configuration.enums.type.ZNConfigType;
import com.google.common.collect.ImmutableMap;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 *
 */
public final class ConfigManager {

    /*
    Configuration list
     */
    private static final ImmutableMap<String, ZNConfig> CONFIG_IMMUTABLE_MAP;

    /*
    The configuration types
     */
    private static final Collection<String> configNames = Arrays.stream(ZNConfigType.values()).map(Enum::name).collect(Collectors.toList());

    static {
        final ImmutableMap.Builder<String, ZNConfig> builder = ImmutableMap.builder();

        for (String name : configNames) {
            try {
                builder.put(name, new ZNConfig(ZNConfigType.valueOf(name), ServersNPC.getPluginFolder().toPath().resolve(String.format("%s.yml", name.toLowerCase()))));
            } catch (IOException e) {
                Logger.getGlobal().log(Level.WARNING, "Could not initialize config for " + name, e);
            }
        }
        CONFIG_IMMUTABLE_MAP = builder.build();
    }

    /**
     * Get configuration by type (ZNConfigType)
     *
     * @param type the config type
     * @return configuration
     */
    public static ZNConfig getByType(ZNConfigType type) {
        return CONFIG_IMMUTABLE_MAP.get(type.name());
    }
}
