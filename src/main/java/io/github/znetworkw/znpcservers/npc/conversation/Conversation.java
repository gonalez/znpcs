package io.github.znetworkw.znpcservers.npc.conversation;

import io.github.znetworkw.znpcservers.configuration.ConfigTypes;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Represents a conversation.
 */
public class Conversation {
    /**
     * The conversation name.
     */
    private final String name;

    /**
     * The conversation texts.
     */
    private final List<ConversationKey> texts;

    /**
     * The radius that a player must be in order to
     * continue/start the conversation with a npc.
     */
    private int radius = 5;

    /**
     * The delay to start the conversation again (in seconds).
     */
    private int delay = 10;

    /**
     * Creates a new empty conversation.
     *
     * @param name The conversation name.
     */
    public Conversation(String name) {
        this(name, new ArrayList<ConversationKey>());
    }

    /**
     * Creates a new conversation.
     *
     * @param name The conversation name.
     * @param text The conversation text.
     */
    public Conversation(String name,
                        Iterable<String> text) {
        this(name, StreamSupport.stream(text.spliterator(), false)
                .map(ConversationKey::new)
                .collect(Collectors.toList()));
    }

    /**
     * Creates a new conversation.
     *
     * @param name The conversation name.
     * @param text The conversation text.
     */
    protected Conversation(String name,
                        List<ConversationKey> text) {
        this.name = name;
        this.texts = text;
    }

    /**
     * Returns the conversation name.
     *
     * @return The conversation name.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the conversation text keys.
     *
     * @return The conversation text keys.
     */
    public List<ConversationKey> getTexts() {
        return texts;
    }

    /**
     * Returns the conversation delay.
     *
     * @return The conversation delay.
     */
    public int getDelay() {
        return delay;
    }

    /**
     * Returns the radius.
     *
     * @return The radius.
     */
    public int getRadius() {
        return radius;
    }

    /**
     * Sets the {@link #getDelay()} of this conversation.
     *
     * @param delay The new delay.
     */
    public void setDelay(int delay) {
        this.delay = delay;
    }

    /**
     * Sets the {@link #getRadius()} of this conversation.
     *
     * @param radius The new radius.
     */
    public void setRadius(int radius) {
        this.radius = radius;
    }

    /**
     * Finds a conversation by its name, if no conversation is found
     * the method will return {@code null}.
     *
     * @param name The conversation name.
     * @return The found conversation.
     */
    public static Conversation forName(String name) {
        return ConfigTypes.NPC_CONVERSATIONS.stream()
                .filter(conversation -> conversation.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }

    /**
     * Returns {@code true} if a conversation with the given name exists.
     *
     * @param name The conversation name.
     * @return If a conversation with the given name exists.
     */
    public static boolean exists(String name) {
        return ConfigTypes.NPC_CONVERSATIONS.stream().anyMatch(conversation -> conversation.getName().equalsIgnoreCase(name));
    }
}
