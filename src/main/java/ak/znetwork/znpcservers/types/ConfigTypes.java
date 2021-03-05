package ak.znetwork.znpcservers.types;

import ak.znetwork.znpcservers.configuration.enums.ZNConfigValue;
import ak.znetwork.znpcservers.configuration.enums.type.ZNConfigType;
import ak.znetwork.znpcservers.manager.ConfigManager;

import ak.znetwork.znpcservers.npc.ZNPC;

import java.util.List;

/**
 * Contains the constants for configuration values.
 *
 * <p>Copyright (c) ZNetwork, 2020.</p>
 *
 * @author ZNetwork
 * @since 07/02/2020
 */
public class ConfigTypes {

    /**
     * Represents the symbol that will be used as spaces for each string.
     */
    public static final String SPACE_SYMBOL = ConfigManager.getByType(ZNConfigType.CONFIG).getValue(ZNConfigValue.REPLACE_SYMBOL);

    /**
     * Represents the render distance for the NPCs.
     */
    public static final int VIEW_DISTANCE = ConfigManager.getByType(ZNConfigType.CONFIG).getValue(ZNConfigValue.VIEW_DISTANCE);

    /**
     * Represents how often the NPCs will be saved.
     */
    public static final int SAVE_DELAY = ConfigManager.getByType(ZNConfigType.CONFIG).getValue(ZNConfigValue.SAVE_NPCS_DELAY_SECONDS);

    /**
     * Represents the npc list.
     */
    public static final List<ZNPC> NPC_LIST = ConfigManager.getByType(ZNConfigType.DATA).getValue(ZNConfigValue.NPC_LIST);

    static {
        // Init all saved NPC
        NPC_LIST.forEach(ZNPC::init);
    }
}
