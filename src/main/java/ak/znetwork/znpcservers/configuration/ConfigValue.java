package ak.znetwork.znpcservers.configuration;

import ak.znetwork.znpcservers.npc.NamingType;
import ak.znetwork.znpcservers.npc.model.ZNPCPojo;

import java.util.ArrayList;

/**
 * <p>Copyright (c) ZNetwork, 2020.</p>
 *
 * @author ZNetwork
 * @since 07/02/2020
 */
public enum ConfigValue {
    /** Data */
    NPC_LIST(ConfigType.DATA, new ArrayList<>(), ZNPCPojo.class),

    /** Config */
    VIEW_DISTANCE(ConfigType.CONFIG, 32, Integer.class), // by Block distance
    REPLACE_SYMBOL(ConfigType.CONFIG, "-", String.class), // Replace spaces symbol , default = " ' "
    SAVE_NPCS_DELAY_SECONDS(ConfigType.CONFIG, 60 * (10), Integer.class), // Save NPC delay (10 minutes)
    MAX_PATH_LOCATIONS(ConfigType.CONFIG, 500, Integer.class),
    NAMING_METHOD(ConfigType.CONFIG, NamingType.DEFAULT, NamingType.class),
    ANIMATION_RGB(ConfigType.CONFIG, false, Boolean.class), // RGB Animation on npc lines...

    /** Messages */
    NO_PERMISSION(ConfigType.MESSAGES, "&cYou do not have permission to execute this command.", String.class),
    SUCCESS(ConfigType.MESSAGES, "&aDone...", String.class),
    INCORRECT_USAGE(ConfigType.MESSAGES, "&cIncorrect use of command.", String.class),
    COMMAND_NOT_FOUND(ConfigType.MESSAGES, "&cThis command was not found.", String.class),
    COMMAND_ERROR(ConfigType.MESSAGES, "&cThere was an error executing the command, see the console for more information.", String.class),
    INVALID_NUMBER(ConfigType.MESSAGES, "&cHey!, The inserted number/id does not look like a number..", String.class),
    NPC_NOT_FOUND(ConfigType.MESSAGES, "&cHey!, I couldnt find a npc with this id.", String.class),
    TOO_FEW_ARGUMENTS(ConfigType.MESSAGES, "&cToo few arguments.", String.class),
    START_PATH(ConfigType.MESSAGES, "&aDone, now walk where you want the npc to, when u finish type /znpcs path -exit.", String.class),
    EXIT_PATH(ConfigType.MESSAGES, "&cYou have exited the waypoint creation.", String.class),
    PATH_FOUND(ConfigType.MESSAGES, "&cThere is already a path with this name.", String.class),
    NPC_FOUND(ConfigType.MESSAGES, "&cThere is already a npc with this id.", String.class),
    NO_PATH_FOUND(ConfigType.MESSAGES, "&cNo path found.", String.class),
    NO_SKIN_FOUND(ConfigType.MESSAGES, "&cSkin not found.", String.class),
    NO_NPC_FOUND(ConfigType.MESSAGES, "&cNo npc found.", String.class),
    NO_ACTION_FOUND(ConfigType.MESSAGES, "&cNo action found.", String.class),
    METHOD_NOT_FOUND(ConfigType.MESSAGES, "&cNo method found.", String.class),
    INVALID_NAME_LENGTH(ConfigType.MESSAGES, "&cThe name is too short or long, it must be in the range of (3 to 16) characters.", String.class),
    UNSUPPORTED_ENTITY(ConfigType.MESSAGES, "&cEntity type not available for your current version.", String.class),
    INCORRECT_USAGE_PATH_SET(ConfigType.MESSAGES, "&cUsage: -set <npc_id> -path <path_name>", String.class),
    INCORRECT_USAGE_ACTION_ADD(ConfigType.MESSAGES, "&cUsage: <SERVER:CMD:MESSAGE:CONSOLE> <actionValue>", String.class),
    INCORRECT_USAGE_ACTION_DELAY(ConfigType.MESSAGES, "&cUsage: <actionID> <delayInSeconds>", String.class);

    /**
     * The configuration type.
     */
    private final ConfigType configType;

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
    ConfigValue(ConfigType znConfigType,
                Object value,
                Class<?> primitiveType) {
        this.configType = znConfigType;
        this.value = value;
        this.primitiveType = primitiveType;
    }

    /**
     * Returns the configuration type.
     *
     * @return The configuration type.
     */
    public ConfigType getConfigType() {
        return configType;
    }


    /**
     * Returns the configuration value.
     *
     * @return The configuration value.
     */
    public Object getValue() {
        return value;
    }

    /**
     * Returns the configuration primitive type.
     *
     * @return The configuration primitive type.
     */
    public Class<?> getPrimitiveType() {
        return primitiveType;
    }
}
