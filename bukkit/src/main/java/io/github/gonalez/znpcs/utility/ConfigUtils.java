package io.github.gonalez.znpcs.utility;

import com.google.common.collect.ImmutableMap;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Function;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

public final class ConfigUtils {

  public static FileConfiguration getOrCreatePluginConfig(
      Plugin plugin, String path) throws IOException {
    return getOrCreatePluginConfig(plugin, path, Function.identity());
  }

  public static <T> T getOrCreatePluginConfig(
      Plugin plugin, String path, Function<FileConfiguration, T> transformer) throws IOException {
    Path configFile = plugin.getDataFolder().toPath().resolve(path);
    if (Files.notExists(configFile)) {
      Files.createDirectories(configFile.getParent());
      try (InputStream in = plugin.getResource(path)) {
        if (in == null) {
          throw new FileNotFoundException("Resource not found in plugin jar: " + path);
        }
        Files.copy(in, configFile);
      }
    }
    FileConfiguration configuration = YamlConfiguration.loadConfiguration(configFile.toFile());
    return transformer.apply(configuration);
  }

  // Returns all nested properties as dot-notation (ie: npc.settings.name)
  public static ImmutableMap<String, String> traverseConfigurationProps(
      FileConfiguration fileConfiguration) {
    ImmutableMap.Builder<String, String> builder = ImmutableMap.builder();
    for (String key : fileConfiguration.getKeys(true)) {
      if (!fileConfiguration.isConfigurationSection(key)) {
        String result = fileConfiguration.getString(key);
        if (result != null) builder.put(key, result);
      }
    }
    return builder.build();
  }

  private ConfigUtils() {}
}
