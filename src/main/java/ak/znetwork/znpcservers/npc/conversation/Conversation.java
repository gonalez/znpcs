package ak.znetwork.znpcservers.npc.conversation;

import ak.znetwork.znpcservers.types.ConfigTypes;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import lombok.Data;

/**
 * <p>Copyright (c) ZNetwork, 2020.</p>
 *
 * @author ZNetwork
 * @since 2/8/2021
 */
@Data
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
     * The radius that the player must be in order
     * To continue the conversation with an npc.
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
     * Finds a npc conversation by its name, if no conversation is found
     * The method will return {@code null}.
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
        return ConfigTypes.NPC_CONVERSATIONS.stream()
                .anyMatch(conversation -> conversation.getName().equalsIgnoreCase(name));
    }
}
