package io.github.gonalez.znpcs;

import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import io.github.gonalez.znpcs.configuration.ConfigConfiguration;
import io.github.gonalez.znpcs.configuration.Configuration;
import io.github.gonalez.znpcs.configuration.ConfigurationManager;
import io.github.gonalez.znpcs.configuration.ConversationsConfiguration;
import io.github.gonalez.znpcs.configuration.DataConfiguration;
import io.github.gonalez.znpcs.configuration.GsonConfigurationManager;
import io.github.gonalez.znpcs.configuration.MessagesConfiguration;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import javax.annotation.Nullable;

public final class ZNPConfigUtils {

  static final ImmutableMap<Class<? extends Configuration>, String> PLUGIN_CONFIGURATIONS =
      ImmutableMap.of(
          ConfigConfiguration.class, "config",
          MessagesConfiguration.class, "messages",
          DataConfiguration.class, "data",
          ConversationsConfiguration.class, "conversations");

  private static final AtomicReference<ConfigurationManager> CONFIG_MANAGER_REF = new AtomicReference<>(null);

  static final Map<Class<? extends Configuration>, Configuration> knownConfigs = new LinkedHashMap<>();

  private ZNPConfigUtils() {}

  private static void setupConfigs(ConfigurationManager configurationManager) {
    for (Class<? extends Configuration> configType : PLUGIN_CONFIGURATIONS.keySet()) {
      Configuration configuration = configurationManager.createConfiguration(
          configType, configurationManager.createDefaultWriter());
      knownConfigs.put(configType, configuration);
    }
  }

  static void setConfigurationManager(ConfigurationManager configurationManager) {
    CONFIG_MANAGER_REF.set(configurationManager);
    setupConfigs(configurationManager);
  }

  public static void rewriteConfigs(Predicate<Configuration> shouldSavePredicate) {
    ConfigurationManager configurationManager = CONFIG_MANAGER_REF.get();
    for (Configuration configuration : knownConfigs.values()) {
      if (shouldSavePredicate.apply(configuration)) {
        configurationManager.writeConfig(configuration, configurationManager.createDefaultWriter());
      }
    }
  }

  @SuppressWarnings("unchecked")
  public static <T extends Configuration> T getConfig(Class<T> configType) {
    if (knownConfigs.containsKey(configType)) {
      return (T) knownConfigs.get(configType);
    }
    throw new NullPointerException("Not a plugin config: " + configType);
  }

  static class PluginConfigConfigurationFormat extends GsonConfigurationManager {
    private final Path pluginFolder;

    public PluginConfigConfigurationFormat(Path pluginFolder, Gson gson) {
      super(gson);
      this.pluginFolder = Preconditions.checkNotNull(pluginFolder);
    }

    @Override
    public void setPath(Class<? extends Configuration> configurationClass, Path path) {
      throw new UnsupportedOperationException("plugin only");
    }

    @Nullable
    @Override
    public Path getPath(Class<? extends Configuration> configurationClass) {
      if (PLUGIN_CONFIGURATIONS.containsKey(configurationClass)) {
        return pluginFolder.resolve(PLUGIN_CONFIGURATIONS.get(configurationClass) + ".json");
      }
      return null;
    }
  }
}
