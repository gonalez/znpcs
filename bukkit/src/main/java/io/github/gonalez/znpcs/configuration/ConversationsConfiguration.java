package io.github.gonalez.znpcs.configuration;

import io.github.gonalez.znpcs.npc.conversation.Conversation;
import java.util.ArrayList;
import java.util.List;

public class ConversationsConfiguration extends Configuration {

  @ConfigurationKey(
      name = "CONVERSATION_LIST",
      description = "List of conversations")
  public List<Conversation> conversationList = new ArrayList<>();
}