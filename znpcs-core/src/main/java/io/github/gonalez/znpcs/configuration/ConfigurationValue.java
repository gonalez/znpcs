package io.github.gonalez.znpcs.configuration;

import static io.github.gonalez.znpcs.utility.GuavaCollectors.toImmutableSet;
import static java.util.stream.Collectors.groupingBy;

import com.google.common.collect.ImmutableSet;
import io.github.gonalez.znpcs.npc.NpcModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

/**
 * @author Gaston Gonzalez {@literal <znetworkw.dev@gmail.com>}
 */
public enum ConfigurationValue {
  /**
   * Npc data.
   */
  NPC_LIST("data", new ArrayList<>(), NpcModel.class),
  /**
   * Configuration.
   */
  VIEW_DISTANCE("config", 32, Integer.class), // by Block distance
  REPLACE_SYMBOL("config", "-", String.class), // replace spaces symbol , default = " ' "
  SAVE_NPCS_DELAY_SECONDS("config", 60 * (10), Integer.class), // save NPC delay (10 minutes)
  MAX_PATH_LOCATIONS("config", 500, Integer.class),
  LINE_SPACING("config", 0.3, Double.class),
  ANIMATION_RGB("config", false, Boolean.class), // RGB Animation on npc lines...
  DEBUG_ENABLED("config", false, Boolean.class), // if should send debug messages to console
  /**
   * Messages
   */
  NO_PERMISSION("messages", "&cYou do not have permission to execute this command.", String.class),
  SUCCESS("messages", "&aDone...", String.class),
  INCORRECT_USAGE("messages", "&cIncorrect use of command.", String.class),
  COMMAND_NOT_FOUND("messages", "&cThis command was not found.", String.class),
  COMMAND_ERROR("messages", "&cThere was an error executing the command, see the console for more information.", String.class),
  INVALID_NUMBER("messages", "&cHey!, The inserted number/id does not look like a number..", String.class),
  NPC_NOT_FOUND("messages", "&cHey!, I couldnt find a npc with this id.", String.class),
  TOO_FEW_ARGUMENTS("messages", "&cToo few arguments.", String.class),
  PATH_START("messages", "&aDone, now walk where you want the npc to, when u finish type /znpcs path exit.", String.class),
  EXIT_PATH("messages", "&cYou have exited the waypoint creation.", String.class),
  PATH_FOUND("messages", "&cThere is already a path with this name.", String.class),
  NPC_FOUND("messages", "&cThere is already a npc with this id.", String.class),
  NO_PATH_FOUND("messages", "&cNo path found.", String.class),
  NO_SKIN_FOUND("messages", "&cSkin not found.", String.class),
  NO_NPC_FOUND("messages", "&cNo npc found.", String.class),
  NO_ACTION_FOUND("messages", "&cNo action found.", String.class),
  METHOD_NOT_FOUND("messages", "&cNo method found.", String.class),
  INVALID_NAME_LENGTH("messages", "&cThe name is too short or long, it must be in the range of (3 to 16) characters.", String.class),
  UNSUPPORTED_ENTITY("messages", "&cEntity type not available for your current version.", String.class),
  PATH_SET_INCORRECT_USAGE("messages", "&eUsage: &aset <npc_id> <path_name>", String.class),
  ACTION_ADD_INCORRECT_USAGE("messages", "&eUsage: &a<SERVER:CMD:MESSAGE:CONSOLE> <actionValue>", String.class),
  ACTION_DELAY_INCORRECT_USAGE("messages", "&eUsage: &a<action_id> <delay>", String.class),
  CONVERSATION_SET_INCORRECT_USAGE("messages", "&cUsage: <npc_id> <conversation_name> <RADIUS:CLICK>", String.class),
  NO_CONVERSATION_FOUND("messages", "&cNo conversation found.", String.class),
  CONVERSATION_FOUND("messages", "&cThere is already a conversation with this name.", String.class),
  INVALID_SIZE("messages", "&cThe position cannot exceed the limit.", String.class),
  CANT_GET_SKIN("messages", "&ccan't fetch skin with name: %s.", String.class),
  GET_SKIN("messages", "&aSkin fetched.", String.class),
  NOT_A_PLAYER("messages", "&cThis npc is not a player.", String.class);

  /**
   * The configuration in which this value will be read / saved.
   */
  private final String configName;
  /**
   *  The key value.
   */
  private final Object value;
  /**
   * The value class type.
   */
  private final Class<?> primitiveType;

  /** values grouped by config name */
  public static final Map<String, ImmutableSet<ConfigurationValue>> VALUES_BY_NAME =
      Arrays.stream(values()).collect(groupingBy(ConfigurationValue::getConfigName, toImmutableSet()));

  /**
   * Creates a new configuration entry.
   *
   * @param configName the configuration name.
   * @param value the key value.
   * @param primitiveType the key value type.
   */
  ConfigurationValue(String configName, Object value, Class<?> primitiveType) {
    this.configName = configName;
    this.value = value;
    this.primitiveType = primitiveType;
  }

  /**
   * Returns the configuration name.
   */
  public String getConfigName() {
    return configName;
  }

  /**
   * Returns the configuration value.
   */
  public Object getValue() {
    return value;
  }

  /**
   * Returns the value primitive type.
   */
  public Class<?> getPrimitiveType() {
    return primitiveType;
  }
}
