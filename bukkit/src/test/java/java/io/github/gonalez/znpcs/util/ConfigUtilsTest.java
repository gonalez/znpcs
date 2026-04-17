package java.io.github.gonalez.znpcs.util;

import static com.google.common.truth.Truth.assertThat;

import com.google.common.collect.ImmutableMap;
import com.google.common.io.Resources;
import io.github.gonalez.znpcs.utility.ConfigUtils;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/** Unit tests for {@link ConfigUtils}. */
@RunWith(JUnit4.class)
public class ConfigUtilsTest {

  @Test
  public void testNestedKeys_dotNotation() throws Exception {
    try (InputStream in =
        Resources.getResource("nested_config.yml").openStream();
        Reader reader = new InputStreamReader(in)) {

      YamlConfiguration config = YamlConfiguration.loadConfiguration(reader);
      ImmutableMap<String, String> flattenedKeys = ConfigUtils.traverseConfigurationProps(config);
      ImmutableMap<String, String> expected =
          ImmutableMap.of(
              "npc.settings.display_name", "SkyWars",
              "npc.settings.type", "PLAYER",
              "npc.data.body.rotation", "90");

      assertThat(flattenedKeys).containsExactlyEntriesIn(expected);
    }
  }
}
