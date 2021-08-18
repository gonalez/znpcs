package ak.znetwork.znpcservers.configuration;

import ak.znetwork.znpcservers.npc.NPC;
import ak.znetwork.znpcservers.npc.NPCModel;
import ak.znetwork.znpcservers.npc.conversation.Conversation;
import ak.znetwork.znpcservers.tasks.NPCLoadTask;

import java.util.List;

/**
 * Configuration constant values used by the plugin.
 */
public final class ConfigTypes {
    /** The symbol that will be used as spaces for each string. */
    public static final String SPACE_SYMBOL = Config.CONFIGURATION.getValue(ConfigValue.REPLACE_SYMBOL);

    /** The render distance for the NPCs. */
    public static final int VIEW_DISTANCE = Config.CONFIGURATION.getValue(ConfigValue.VIEW_DISTANCE);

    /** How often the NPCs will be saved. in seconds */
    public static final int SAVE_DELAY = Config.CONFIGURATION.getValue(ConfigValue.SAVE_NPCS_DELAY_SECONDS);

    /** Determines if rgb animation should be used. */
    public static final boolean RGB_ANIMATION = Config.CONFIGURATION.getValue(ConfigValue.ANIMATION_RGB);

    /** The npc list. */
    public static final List<NPCModel> NPC_LIST = Config.DATA.getValue(ConfigValue.NPC_LIST);

    /** The npc conversation list. */
    public static final List<Conversation> NPC_CONVERSATIONS = Config.CONVERSATIONS.getValue(ConfigValue.CONVERSATION_LIST);

    static {
        NPC_LIST.stream()
                .map(NPC::new)
                .forEach(NPCLoadTask::new); // initialize all saved NPC...
    }

    /** Default constructor */
    private ConfigTypes() {
        throw new AssertionError("This class is not intended to be initialized.");
    }
}
