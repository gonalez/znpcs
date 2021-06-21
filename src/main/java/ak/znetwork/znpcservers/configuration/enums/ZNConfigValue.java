package ak.znetwork.znpcservers.configuration.enums;

import ak.znetwork.znpcservers.configuration.enums.type.ZNConfigType;
import ak.znetwork.znpcservers.npc.enums.NamingType;
import ak.znetwork.znpcservers.npc.model.ZNPCPojo;

import java.util.ArrayList;

/**
 * <p>Copyright (c) ZNetwork, 2020.</p>
 *
 * @author ZNetwork
 * @since 07/02/2020
 */
public enum ZNConfigValue {

    // NPC
    NPC_LIST(ZNConfigType.DATA, new ArrayList<>(), ZNPCPojo.class),

    // Config
    VIEW_DISTANCE(ZNConfigType.CONFIG, 32, Integer.class), // by Block distance
    REPLACE_SYMBOL(ZNConfigType.CONFIG, "-", String.class), // Replace spaces symbol , default = " ' "
    SAVE_NPCS_DELAY_SECONDS(ZNConfigType.CONFIG, 60 * (10), Integer.class), // Save NPC delay (10 minutes)
    MAX_PATH_LOCATIONS(ZNConfigType.CONFIG, 500, Integer.class),
    NAMING_METHOD(ZNConfigType.CONFIG, NamingType.DEFAULT, NamingType.class),

    // Messages
    NO_PERMISSION(ZNConfigType.MESSAGES, "&cYou do not have permission to execute this command.", String.class),
    SUCCESS(ZNConfigType.MESSAGES, "&aDone...", String.class),
    INCORRECT_USAGE(ZNConfigType.MESSAGES, "&cIncorrect use of command.", String.class),
    COMMAND_NOT_FOUND(ZNConfigType.MESSAGES, "&cThis command was not found.", String.class),
    COMMAND_ERROR(ZNConfigType.MESSAGES, "&cThere was an error executing the command, see the console for more information.", String.class),
    INVALID_NUMBER(ZNConfigType.MESSAGES, "&cHey!, The inserted number/id does not look like a number..", String.class),
    NPC_NOT_FOUND(ZNConfigType.MESSAGES, "&cHey!, I couldnt find a npc with this id.", String.class),
    TOO_FEW_ARGUMENTS(ZNConfigType.MESSAGES, "&cToo few arguments.", String.class),
    START_PATH(ZNConfigType.MESSAGES, "&aDone, now walk where you want the npc to, when u finish type /znpcs path -exit.", String.class),
    EXIT_PATH(ZNConfigType.MESSAGES, "&cYou have exited the waypoint creation.", String.class),
    PATH_FOUND(ZNConfigType.MESSAGES, "&cThere is already a path with this name.", String.class),
    NPC_FOUND(ZNConfigType.MESSAGES, "&cThere is already a npc with this id.", String.class),
    NO_PATH_FOUND(ZNConfigType.MESSAGES, "&cNo path found.", String.class),
    NO_NPC_FOUND(ZNConfigType.MESSAGES, "&cNo npc found.", String.class),
    NO_ACTION_FOUND(ZNConfigType.MESSAGES, "&cNo action found.", String.class),
    METHOD_NOT_FOUND(ZNConfigType.MESSAGES, "&cNo method found.", String.class),
    INVALID_NAME_LENGTH(ZNConfigType.MESSAGES, "&cThe name is too short or long, it must be in the range of (3 to 16) characters.", String.class),
    UNSUPPORTED_ENTITY(ZNConfigType.MESSAGES, "&cEntity type not available for your current version.", String.class),
    INCORRECT_USAGE_PATH_SET(ZNConfigType.MESSAGES, "&cUsage: -set <npc_id> -path <path_name>", String.class),
    INCORRECT_USAGE_ACTION_ADD(ZNConfigType.MESSAGES, "&cUsage: <SERVER:CMD:MESSAGE:CONSOLE> <actionValue>", String.class),
    INCORRECT_USAGE_ACTION_DELAY(ZNConfigType.MESSAGES, "&cUsage: <actionID> <delayInSeconds>", String.class);

    /**
     * The configuration type.
     */
    private final ZNConfigType configType;

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
    ZNConfigValue(ZNConfigType znConfigType,
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
    public ZNConfigType getConfigType() {
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
