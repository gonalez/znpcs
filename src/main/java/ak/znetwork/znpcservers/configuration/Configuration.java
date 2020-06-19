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

  public String string;

  public Configuration(final Plugin core, String string) {
    this.core = core;

    this.string = string;

    if (!core.getDataFolder().exists()) {
      core.getDataFolder().mkdir();
    }

    file = new File(core.getDataFolder(), string + ".yml");


    if (!file.exists()) {
      file.getParentFile().mkdirs();
    }

    configuration = new YamlConfiguration();

    core.saveResource(string + ".yml", false);

    try {
      configuration.load(file);
    } catch (IOException | InvalidConfigurationException e) {
      e.printStackTrace();
    }
  }

  public FileConfiguration getConfig() {
    return configuration;
  }

  public File getFile() {
    return file;
  }

  public void save(){
    try {
      configuration.save(getFile());
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
