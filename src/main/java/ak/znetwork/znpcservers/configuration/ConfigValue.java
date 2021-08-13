package ak.znetwork.znpcservers.configuration;

import ak.znetwork.znpcservers.npc.NamingType;
import ak.znetwork.znpcservers.npc.NPCModel;
import ak.znetwork.znpcservers.npc.conversation.Conversation;

import java.util.ArrayList;

/**
 * The configuration values.
 */
public enum ConfigValue {
    /** Data */
    NPC_LIST(ConfigKey.DATA, new ArrayList<>(), NPCModel.class),
    /** Config */
    VIEW_DISTANCE(ConfigKey.CONFIG, 32, Integer.class), // by Block distance
    REPLACE_SYMBOL(ConfigKey.CONFIG, "-", String.class), // replace spaces symbol , default = " ' "
    SAVE_NPCS_DELAY_SECONDS(ConfigKey.CONFIG, 60 * (10), Integer.class), // save NPC delay (10 minutes)
    MAX_PATH_LOCATIONS(ConfigKey.CONFIG, 500, Integer.class),
    NAMING_METHOD(ConfigKey.CONFIG, NamingType.DEFAULT, NamingType.class),
    LINE_SPACING(ConfigKey.CONFIG, 0.3, Double.class),
    ANIMATION_RGB(ConfigKey.CONFIG, false, Boolean.class), // RGB Animation on npc lines...
    /** Messages */
    NO_PERMISSION(ConfigKey.MESSAGES, "&cYou do not have permission to execute this command.", String.class),
    SUCCESS(ConfigKey.MESSAGES, "&aDone...", String.class),
    INCORRECT_USAGE(ConfigKey.MESSAGES, "&cIncorrect use of command.", String.class),
    COMMAND_NOT_FOUND(ConfigKey.MESSAGES, "&cThis command was not found.", String.class),
    COMMAND_ERROR(ConfigKey.MESSAGES, "&cThere was an error executing the command, see the console for more information.", String.class),
    INVALID_NUMBER(ConfigKey.MESSAGES, "&cHey!, The inserted number/id does not look like a number..", String.class),
    NPC_NOT_FOUND(ConfigKey.MESSAGES, "&cHey!, I couldnt find a npc with this id.", String.class),
    TOO_FEW_ARGUMENTS(ConfigKey.MESSAGES, "&cToo few arguments.", String.class),
    PATH_START(ConfigKey.MESSAGES, "&aDone, now walk where you want the npc to, when u finish type /znpcs path exit.", String.class),
    EXIT_PATH(ConfigKey.MESSAGES, "&cYou have exited the waypoint creation.", String.class),
    PATH_FOUND(ConfigKey.MESSAGES, "&cThere is already a path with this name.", String.class),
    NPC_FOUND(ConfigKey.MESSAGES, "&cThere is already a npc with this id.", String.class),
    NO_PATH_FOUND(ConfigKey.MESSAGES, "&cNo path found.", String.class),
    NO_SKIN_FOUND(ConfigKey.MESSAGES, "&cSkin not found.", String.class),
    NO_NPC_FOUND(ConfigKey.MESSAGES, "&cNo npc found.", String.class),
    NO_ACTION_FOUND(ConfigKey.MESSAGES, "&cNo action found.", String.class),
    METHOD_NOT_FOUND(ConfigKey.MESSAGES, "&cNo method found.", String.class),
    INVALID_NAME_LENGTH(ConfigKey.MESSAGES, "&cThe name is too short or long, it must be in the range of (3 to 16) characters.", String.class),
    UNSUPPORTED_ENTITY(ConfigKey.MESSAGES, "&cEntity type not available for your current version.", String.class),
    PATH_SET_INCORRECT_USAGE(ConfigKey.MESSAGES, "&eUsage: &aset <npc_id> <path_name>", String.class),
    ACTION_ADD_INCORRECT_USAGE(ConfigKey.MESSAGES, "&eUsage: &a<SERVER:CMD:MESSAGE:CONSOLE> <actionValue>", String.class),
    ACTION_DELAY_INCORRECT_USAGE(ConfigKey.MESSAGES, "&eUsage: &a<action_id> <delay>", String.class),
    CONVERSATION_SET_INCORRECT_USAGE(ConfigKey.MESSAGES, "&cUsage: <npc_id> <conversation_name> <RADIUS:CLICK>", String.class),
    NO_CONVERSATION_FOUND(ConfigKey.MESSAGES, "&cNo conversation found.", String.class),
    CONVERSATION_FOUND(ConfigKey.MESSAGES, "&cThere is already a conversation with this name.", String.class),
    INVALID_SIZE(ConfigKey.MESSAGES, "&cThe position cannot exceed the limit.", String.class),
    /** Conversations */
    CONVERSATION_LIST(ConfigKey.CONVERSATIONS, new ArrayList<>(), Conversation.class);

    /**
     * The configuration type.
     */
    private final ConfigKey configType;

    /**
     * The configuration value.
     */
    private final Object value;

    /**
     * The configuration primitive equivalent;
     */
    private final Class<?> primitiveType;

    /**
     * Creates a new configuration entry.
     *
     * @param znConfigType The configuration type.
     * @param value The configuration value.
     * @param primitiveType The configuration primitive type.
     */
    ConfigValue(ConfigKey znConfigType,
                Object value,
                Class<?> primitiveType) {
        this.configType = znConfigType;
        this.value = value;
        this.primitiveType = primitiveType;
    }

    /**
     * Returns the value config type type.
     *
     * @return The value config type type.
     */
    public ConfigKey getConfigType() {
        return configType;
    }


    /**
     * Returns the value.
     *
     * @return The value.
     */
    public Object getValue() {
        return value;
    }

    /**
     * Returns the value primitive type.
     *
     * @return The value primitive type.
     */
    public Class<?> getPrimitiveType() {
        return primitiveType;
    }
}
