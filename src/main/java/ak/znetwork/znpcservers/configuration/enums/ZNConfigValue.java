package ak.znetwork.znpcservers.configuration.enums;

import ak.znetwork.znpcservers.configuration.enums.type.ZNConfigType;
import ak.znetwork.znpcservers.npc.ZNPC;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;

import lombok.Getter;


/**
 * <p>Copyright (c) ZNetwork, 2020.</p>
 *
 * @author ZNetwork
 * @since 07/02/2020
 */
@Getter
public enum ZNConfigValue {

    // NPC
    NPC_LIST(ZNConfigType.DATA, new ArrayList<>(), ZNPC.class),

    // Config
    VIEW_DISTANCE(ZNConfigType.CONFIG, 32, Integer.class), // by Block distance
    REPLACE_SYMBOL(ZNConfigType.CONFIG, "-", String.class), // Replace spaces symbol , default = " ' "
    SAVE_NPCS_DELAY_SECONDS(ZNConfigType.CONFIG, 60 * (10), Integer.class), // Save NPC delay (10 minutes)
    MAX_PATH_LOCATIONS(ZNConfigType.CONFIG, 500, Integer.class),

    // Messages
    NO_PERMISSION(ZNConfigType.MESSAGES, "&cYou do not have permission to execute this command.", String.class),
    SUCCESS(ZNConfigType.MESSAGES, "&aDone...", String.class),
    INCORRECT_USAGE(ZNConfigType.MESSAGES, "&cIncorrect use of command.", String.class),
    COMMAND_NOT_FOUND(ZNConfigType.MESSAGES, "&cThis command was not found.", String.class),
    COMMAND_ERROR(ZNConfigType.MESSAGES, "&cThere was an error executing the command, see the console for more information.", String.class),
    INVALID_NUMBER(ZNConfigType.MESSAGES, "&cHey!, The inserted number/id does not look like a number..", String.class),
    NPC_NOT_FOUND(ZNConfigType.MESSAGES, "&cHey!, I couldnt find a npc with this id.", String.class);

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
}
