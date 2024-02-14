package io.github.gonalez.znpcs.configuration;

import static com.google.gson.internal.$Gson$Types.newParameterizedTypeWithOwner;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import io.github.gonalez.znpcs.ServersNPC;
import io.github.gonalez.znpcs.utility.Utils;
import org.bukkit.command.CommandSender;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.Map;
import java.util.stream.Collectors;

public class Configuration {
  static final String CONFIG_FORMAT = ".json";
  
  private static final JsonParser JSON_PARSER = new JsonParser();
  
  private static final Charset CHARSET = StandardCharsets.UTF_8;
  
  private final String name;
  
  private final Path path;
  
  private final Map<ConfigurationValue, Object> configurationValues;
  
  public static final Configuration CONFIGURATION = new Configuration("config");
  
  public static final Configuration MESSAGES = new Configuration("messages");
  
  public static final Configuration CONVERSATIONS = new Configuration("conversations");
  
  public static final Configuration DATA = new Configuration("data");
  
  public static final ImmutableList<Configuration> SAVE_CONFIGURATIONS = ImmutableList.of(CONVERSATIONS, DATA);
  
  protected Configuration(String name) {
    this(name, ServersNPC.PLUGIN_FOLDER.toPath().resolve(name + ".json"));
  }
  
  private Configuration(String name, Path path) {
    if (!path.getFileName().toString().endsWith(".json"))
      throw new IllegalStateException("invalid configuration format for: " + path.getFileName()); 
    this.name = name;
    this.path = path;
    this
      
      .configurationValues = (Map<ConfigurationValue, Object>)((ImmutableSet)ConfigurationValue.VALUES_BY_NAME.get(name)).stream().collect(Collectors.toMap(c -> c, ConfigurationValue::getValue));
    onLoad();
  }
  
  protected void onLoad() {
    synchronized (this.path) {
      try {
        Reader reader = Files.newBufferedReader(this.path, CHARSET);
        try {
          JsonElement data = JSON_PARSER.parse(reader);
          if (data == null) {
            if (reader != null)
              reader.close(); 
            return;
          } 
          for (ConfigurationValue configValue : this.configurationValues.keySet()) {
            boolean single = (this.configurationValues.size() == 1);
            JsonElement jsonElement = single ? data : (data.isJsonObject() ? data.getAsJsonObject().get(configValue.name()) : null);
            if (jsonElement != null && !jsonElement.isJsonNull()) {
              if (!single && configValue.getPrimitiveType().isEnum()) {
                this.configurationValues.put(configValue, ServersNPC.GSON.fromJson(jsonElement, configValue.getPrimitiveType()));
                continue;
              } 
              this.configurationValues.put(configValue, ServersNPC.GSON.fromJson(jsonElement, newParameterizedTypeWithOwner(null, configValue.getValue().getClass(), configValue.getPrimitiveType())));
            } 
          } 
          if (reader != null)
            reader.close(); 
        } catch (Throwable throwable) {
          if (reader != null)
            try {
              reader.close();
            } catch (Throwable throwable1) {
              throwable.addSuppressed(throwable1);
            }  
          throw throwable;
        } 
      } catch (NoSuchFileException noSuchFileException) {
      
      } catch (IOException e) {
        throw new IllegalStateException("Failed to read config: " + this.name);
      } finally {
        save();
      } 
    } 
  }
  
  public void save() {
    synchronized (this.path) {
      try {
        Writer writer = Files.newBufferedWriter(this.path, CHARSET);
        try {
          ServersNPC.GSON.toJson((this.configurationValues.size() == 1) ? 
              this.configurationValues.values().iterator().next() : this.configurationValues, writer);
          if (writer != null)
            writer.close(); 
        } catch (Throwable throwable) {
          if (writer != null)
            try {
              writer.close();
            } catch (Throwable throwable1) {
              throwable.addSuppressed(throwable1);
            }  
          throw throwable;
        } 
      } catch (IOException e) {
        throw new IllegalStateException("Failed to save config: " + this.name);
      } 
    } 
  }
  
  public <T> T getValue(ConfigurationValue configValue) {
    synchronized (this.path) {
      return (T)this.configurationValues.get(configValue);
    } 
  }

  public void sendMessage(CommandSender sender, ConfigurationValue configValue, Object... replaces) {
    sender.sendMessage(Utils.toColor(String.format(getValue(configValue), replaces)));
  }
}
