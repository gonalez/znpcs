package io.github.gonalez.znpcs.user;

import static com.google.common.truth.Truth.assertThat;

import io.github.gonalez.znpcs.configuration.Configuration;
import io.github.gonalez.znpcs.configuration.ConfigurationKey;
import io.github.gonalez.znpcs.configuration.ConfigurationProvider;
import io.github.gonalez.znpcs.configuration.SimpleConfigurationProvider;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/** Unit tests for {@link User}. */
@RunWith(JUnit4.class)
public class UserTest {
  ByteArrayOutputStream output;
  PrintStream input;

  ConfigurationProvider configurationProvider;

  private static class TestExampleConfig extends Configuration {

    @ConfigurationKey(name = "car_brand")
    public String carBrand = "mercedes";
  }

  @BeforeEach
  public final void createStreams() throws Exception  {
    output = new ByteArrayOutputStream();
    input = new PrintStream(output);

    configurationProvider =
        SimpleConfigurationProvider.newBuilder()
            .addConfiguration(TestExampleConfig.class, new TestExampleConfig())
            .build();
  }

  private class PrintConfigTestUser extends AbstractUser {

    public PrintConfigTestUser(
        ConfigurationProvider configurationProvider,
        Class<? extends Configuration> definedTextConfiguration) {
      super(UUID.randomUUID(), configurationProvider, definedTextConfiguration);
    }

    @Override
    public void sendChatText(String text) {
      input.print(text);
    }
  }

  @Test
  public void testTest() throws Exception {
    PrintConfigTestUser testUser = new PrintConfigTestUser(configurationProvider, TestExampleConfig.class);
    testUser.sendDefinedText("car_brand");
    assertThat("mercedes").isEqualTo(output.toString(StandardCharsets.UTF_8).trim());
  }
}
