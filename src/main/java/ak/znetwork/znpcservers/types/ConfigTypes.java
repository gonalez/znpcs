package ak.znetwork.znpcservers.types;

import ak.znetwork.znpcservers.configuration.ConfigValue;
import ak.znetwork.znpcservers.configuration.ConfigType;
import ak.znetwork.znpcservers.manager.ConfigManager;

import ak.znetwork.znpcservers.npc.ZNPC;
import ak.znetwork.znpcservers.npc.conversation.Conversation;
import ak.znetwork.znpcservers.npc.ZNPCModel;
import ak.znetwork.znpcservers.tasks.NPCLoadTask;

import java.util.List;

/**
 * Contains the constants for configuration values.
 *
 * <p>Copyright (c) ZNetwork, 2020.</p>
 *
 * @author ZNetwork
 * @since 07/02/2020
 */
public final class ConfigTypes {
    /**
     * Represents the symbol that will be used as spaces for each string.
     */
    public static final String SPACE_SYMBOL = ConfigManager.getByType(ConfigType.CONFIG).getValue(ConfigValue.REPLACE_SYMBOL);

    /**
     * Represents the render distance for the NPCs.
     */
    public static final int VIEW_DISTANCE = ConfigManager.getByType(ConfigType.CONFIG).getValue(ConfigValue.VIEW_DISTANCE);

    /**
     * Represents how often the NPCs will be saved.
     */
    public static final int SAVE_DELAY = ConfigManager.getByType(ConfigType.CONFIG).getValue(ConfigValue.SAVE_NPCS_DELAY_SECONDS);

    /**
     * Represents the npc name in tab-list.
     */
    public static final boolean RGB_ANIMATION = ConfigManager.getByType(ConfigType.CONFIG).getValue(ConfigValue.ANIMATION_RGB);

    /**
     * Represents the npc list.
     */
    public static final List<ZNPCModel> NPC_LIST = ConfigManager.getByType(ConfigType.DATA).getValue(ConfigValue.NPC_LIST);

    /**
     * Represents the npc conversation list.
     */
    public static final List<Conversation> NPC_CONVERSATIONS = ConfigManager.getByType(ConfigType.CONVERSATIONS).getValue(ConfigValue.CONVERSATION_LIST);

    static {
        // Initialize all saved NPC...
        NPC_LIST.stream()
                .map(ZNPC::new)
                .forEach(NPCLoadTask::new);
    }

    /** Default constructor */
    private ConfigTypes() {
        throw new AssertionError("This class is not intended to be initialized.");
    }
}
