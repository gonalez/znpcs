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
package ak.znetwork.znpcservers.configuration;

import ak.znetwork.znpcservers.configuration.enums.ZNConfigValue;
import ak.znetwork.znpcservers.configuration.enums.type.ZNConfigType;
import ak.znetwork.znpcservers.configuration.impl.ZNConfigInterface;
import ak.znetwork.znpcservers.utils.Utils;
import org.bukkit.command.CommandSender;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.EnumMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Configuration
 *
 * @author ZNetwork
 *
 *
 * TODO
 * - -
 */
public class ZNConfig implements ZNConfigInterface {

    private final Path path;

    private final ZNConfigType znConfigType;

    private final EnumMap<ZNConfigValue, Object> configValueStringEnumMap;

    private final Yaml yaml = getYaml();

    public ZNConfig(final ZNConfigType znConfigType, final Path path) throws IOException {
        this.znConfigType = znConfigType;

        this.path = path;

        this.configValueStringEnumMap = new EnumMap<>(ZNConfigValue.class);

        final File file = new File(path.toUri());
        if (!file.exists()) file.createNewFile();

        this.load();
    }

    @Override
    public void load() throws IOException {
        this.configValueStringEnumMap.clear();

        try (BufferedReader reader = Files.newBufferedReader(this.path, StandardCharsets.UTF_8)) {
            Map<String, Object> data = yaml.load(reader);

            if (data != null && !data.isEmpty()) {
                for (Map.Entry<String, Object> entry : data.entrySet()) {
                    if (entry.getKey() == null || entry.getKey().isEmpty()) continue;

                    try {
                        ZNConfigValue znConfigValue = ZNConfigValue.valueOf(entry.getKey());

                        if (!entry.getValue().getClass().isAssignableFrom(znConfigValue.ext)) continue;
                        configValueStringEnumMap.put(znConfigValue, entry.getValue());
                    } catch (IllegalArgumentException exception) {
                    } // It is not a Config Value (@ZNConfigValue)
                }
            }

            // Default values check
            for (ZNConfigValue znConfigValue : ZNConfigValue.values())
                if (!configValueStringEnumMap.containsKey(znConfigValue) && znConfigValue.znConfigType == this.znConfigType)
                    configValueStringEnumMap.put(znConfigValue, znConfigValue.value); // Default

            // Save to file
            save(configValueStringEnumMap.entrySet().stream().collect(Collectors.toMap(key -> key.getKey().name(), Map.Entry::getValue)));
        }
    }

    @Override
    public void save(Map<Object, Object> hashMap) throws IOException {
        try (FileWriter writer = new FileWriter(new File(path.toUri()))) {
            yaml.dump(hashMap, writer);
        }
    }

    @Override
    public Yaml getYaml() {
        DumperOptions options = new DumperOptions();
        options.setPrettyFlow(true);
        return new Yaml(options);
    }


    @Override
    public void sendMessage(CommandSender player, ZNConfigValue znConfigValue) {
        String value = getValue(znConfigValue);

        if (value != null) player.sendMessage(Utils.color(value));
    }

    @Override
    public String getValue(ZNConfigValue znConfigValue) {
        return String.valueOf(this.configValueStringEnumMap.get(znConfigValue));
    }
}
