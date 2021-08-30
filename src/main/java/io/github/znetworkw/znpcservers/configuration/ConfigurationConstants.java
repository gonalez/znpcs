package io.github.znetworkw.znpcservers.configuration;

import io.github.znetworkw.znpcservers.npc.NPC;
import io.github.znetworkw.znpcservers.npc.NPCModel;
import io.github.znetworkw.znpcservers.npc.conversation.Conversation;
import io.github.znetworkw.znpcservers.npc.task.NPCLoadTask;

import java.util.List;

/**
 * Configuration constant values used by the plugin.
 */
public final class ConfigurationConstants {
    /** The symbol that will be used as spaces for each string. */
    public static final String SPACE_SYMBOL = Configuration.CONFIGURATION.getValue(ConfigurationValue.REPLACE_SYMBOL);
    /** The render distance for the NPCs. */
    public static final int VIEW_DISTANCE = Configuration.CONFIGURATION.getValue(ConfigurationValue.VIEW_DISTANCE);
    /** How often the NPCs will be saved. in seconds */
    public static final int SAVE_DELAY = Configuration.CONFIGURATION.getValue(ConfigurationValue.SAVE_NPCS_DELAY_SECONDS);
    /** Determines if rgb animation should be used. */
    public static final boolean RGB_ANIMATION = Configuration.CONFIGURATION.getValue(ConfigurationValue.ANIMATION_RGB);
    /** The npc list. */
    public static final List<NPCModel> NPC_LIST = Configuration.DATA.getValue(ConfigurationValue.NPC_LIST);
    /** The npc conversation list. */
    public static final List<Conversation> NPC_CONVERSATIONS = Configuration.CONVERSATIONS.getValue(ConfigurationValue.CONVERSATION_LIST);

    static {
        NPC_LIST.stream()
            .map(NPC::new)
            .forEach(NPCLoadTask::new); // initialize all saved NPC...
    }

    private ConfigurationConstants() {}
}
