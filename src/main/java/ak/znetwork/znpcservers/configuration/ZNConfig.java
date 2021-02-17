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
import ak.znetwork.znpcservers.configuration.impl.ZNConfigImpl;
import ak.znetwork.znpcservers.utils.Utils;
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
 * Configuration
 *
 * @author ZNetwork
 * <p>
 * <p>
 * TODO
 * - -
 */
public final class ZNConfig implements ZNConfigImpl {

    private final Path path;

    private final ZNConfigType znConfigType;

    private final EnumMap<ZNConfigValue, Object> configValueStringEnumMap;

    private final Yaml yaml = getYaml();

    public ZNConfig(final ZNConfigType znConfigType, final Path path) {
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

        try (BufferedReader reader = Files.newBufferedReader(this.path, StandardCharsets.UTF_8)) {
            Map<String, Object> data = yaml.load(reader);

            if (data != null && !data.isEmpty()) {
                for (Map.Entry<String, Object> entry : data.entrySet()) {
                    if (entry.getKey() == null || entry.getKey().isEmpty()) continue;

                    ZNConfigValue znConfigValue = ZNConfigValue.valueOf(entry.getKey());
                    if (!entry.getValue().getClass().isAssignableFrom(znConfigValue.getClazz())) continue;

                    configValueStringEnumMap.put(znConfigValue, entry.getValue());
                }
            }

            // Default values check
            for (ZNConfigValue znConfigValue : ZNConfigValue.values())
                if (!configValueStringEnumMap.containsKey(znConfigValue) && znConfigValue.getConfigType() == this.znConfigType)
                    configValueStringEnumMap.put(znConfigValue, znConfigValue.getValue()); // Default

            // Save to file
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
    public Yaml getYaml() {
        DumperOptions options = new DumperOptions();
        options.setPrettyFlow(true);
        return new Yaml(options);
    }


    @Override
    public void sendMessage(CommandSender player, ZNConfigValue znConfigValue) {
        player.sendMessage(Utils.color(this.getValue(znConfigValue)));
    }

    @Override
    public <T> T getValue(ZNConfigValue znConfigValue) {
        return (T) this.configValueStringEnumMap.get(znConfigValue);
    }
}
