package io.github.gonalez.znpcs.npc.conversation;

import io.github.gonalez.znpcs.ZNPConfigUtils;
import io.github.gonalez.znpcs.configuration.ConversationsConfiguration;
import java.util.ArrayList;
import java.util.List;

public class Conversation {
  private final String name;

  private final List<ConversationKey> texts;

  private int radius = 5;

  private int delay = 10;

  public Conversation(String name) {
    this(name, new ArrayList<>());
  }

  protected Conversation(String name, List<ConversationKey> text) {
    this.name = name;
    this.texts = text;
  }

  public String getName() {
    return this.name;
  }

  public List<ConversationKey> getTexts() {
    return this.texts;
  }

  public int getDelay() {
    return this.delay;
  }

  public int getRadius() {
    return this.radius;
  }

  public void setDelay(int delay) {
    this.delay = delay;
  }

  public void setRadius(int radius) {
    this.radius = radius;
  }

  public static Conversation forName(String name) {
    return ZNPConfigUtils.getConfig(ConversationsConfiguration.class).conversationList.stream()
      .filter(conversation -> conversation.getName().equalsIgnoreCase(name))
      .findFirst()
      .orElse(null);
  }

  public static boolean exists(String name) {
    return ZNPConfigUtils.getConfig(ConversationsConfiguration.class).conversationList.stream()
      .anyMatch(conversation -> conversation.getName().equalsIgnoreCase(name));
  }
}
