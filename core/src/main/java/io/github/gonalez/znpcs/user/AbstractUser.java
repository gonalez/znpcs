package io.github.gonalez.znpcs.user;

import static com.google.common.base.Preconditions.checkNotNull;

import io.github.gonalez.znpcs.configuration.Configuration;
import io.github.gonalez.znpcs.configuration.ConfigurationProvider;
import java.util.UUID;

public abstract class AbstractUser implements User {
  private final UUID uuid;
  private final ConfigurationProvider configurationProvider;
  private final Class<? extends Configuration> definedTextConfiguration;

  public AbstractUser(
      UUID uuid,
      ConfigurationProvider configurationProvider,
      Class<? extends Configuration> definedTextConfiguration) {
    this.uuid = checkNotNull(uuid);
    this.configurationProvider = checkNotNull(configurationProvider);
    this.definedTextConfiguration = checkNotNull(definedTextConfiguration);
  }

  @Override
  public UUID getId() {
    return uuid;
  }

  @Override
  public void sendDefinedText(String text) {
    Configuration configuration = configurationProvider.provideConfiguration(definedTextConfiguration);
    if (configuration != null) {
      Object value = configuration.getFieldMap().get(text);
      if (value != null) {
        sendChatText(value.toString());
      }
    }
  }
}
