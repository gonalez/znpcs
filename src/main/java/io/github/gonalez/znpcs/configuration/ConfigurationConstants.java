package io.github.gonalez.znpcs.configuration;

import io.github.gonalez.znpcs.npc.NPC;
import io.github.gonalez.znpcs.npc.NPCModel;
import io.github.gonalez.znpcs.npc.conversation.Conversation;
import io.github.gonalez.znpcs.npc.task.NPCLoadTask;

import java.util.List;

public final class ConfigurationConstants {
  public static final String SPACE_SYMBOL = Configuration.CONFIGURATION.getValue(ConfigurationValue.REPLACE_SYMBOL);
  
  public static final int VIEW_DISTANCE = Configuration.CONFIGURATION.<Integer>getValue(ConfigurationValue.VIEW_DISTANCE);
  
  public static final int SAVE_DELAY = Configuration.CONFIGURATION.<Integer>getValue(ConfigurationValue.SAVE_NPCS_DELAY_SECONDS);
  
  public static final boolean RGB_ANIMATION = Configuration.CONFIGURATION.<Boolean>getValue(ConfigurationValue.ANIMATION_RGB);
  
  public static final List<NPCModel> NPC_LIST = Configuration.DATA.getValue(ConfigurationValue.NPC_LIST);
  
  public static final List<Conversation> NPC_CONVERSATIONS = Configuration.CONVERSATIONS.getValue(ConfigurationValue.CONVERSATION_LIST);
  
  static {
    NPC_LIST.stream()
      .map(NPC::new)
      .forEach(NPCLoadTask::new);
  }
}
