package ak.znetwork.znpcservers.configuration;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;

public class Configuration {

  private FileConfiguration configuration;
  private File file;

  public Plugin core;

  protected final String name;

  public Configuration(final Plugin core, String name) {
    this.core = core;
    this.name = name;

    if (!core.getDataFolder().exists()) {
      core.getDataFolder().mkdir();
    }

    this.file = new File(core.getDataFolder(), this.name + ".yml");

    configuration = new YamlConfiguration();

    if (!file.exists()) {
      file.getParentFile().mkdirs();

      core.saveResource(this.name + ".yml", false);
    }

    try {
      configuration.load(file);
    } catch (IOException | InvalidConfigurationException e) {
      e.printStackTrace();
    }
  }

  public FileConfiguration getConfig() {
    return configuration;
  }

  public void save(){
    try {
      configuration.save(this.file);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
