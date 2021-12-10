package io.github.znetworkw.znpcservers.configuration;

import io.github.znetworkw.znpcservers.npc.NpcModel;

import java.util.List;

/**
 * Configuration constant values used by the plugin.
 *
 * @author Gaston Gonzalez {@literal <znetworkw.dev@gmail.com>}
 */
public final class ConfigurationConstants {
  /**
   * The character that will be used as spaces for each string.
   */
  public static final String SPACE_SYMBOL = Configuration.CONFIGURATION.getValue(ConfigurationValue.REPLACE_SYMBOL);
  /**
   * The render distance for the NPCs.
   */
  public static final int VIEW_DISTANCE = Configuration.CONFIGURATION.getValue(ConfigurationValue.VIEW_DISTANCE);
  /**
   * How often the npcs should be saved. (in seconds)
   */
  public static final int SAVE_DELAY = Configuration.CONFIGURATION.getValue(ConfigurationValue.SAVE_NPCS_DELAY_SECONDS);

  public static final boolean DEBUG_ENABLED = Configuration.CONFIGURATION.getValue(ConfigurationValue.DEBUG_ENABLED);

  public static final List<NpcModel> NPC_MODELS = Configuration.DATA.getValue(ConfigurationValue.NPC_LIST);

  private ConfigurationConstants() {}
}
