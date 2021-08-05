package ak.znetwork.znpcservers.npc.conversation;

import ak.znetwork.znpcservers.npc.ZNPC;
import ak.znetwork.znpcservers.utility.Utils;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Stream;

/**
 * <p>Copyright (c) ZNetwork, 2020.</p>
 *
 * @author ZNetwork
 * @since 4/8/2021
 */
public class ConversationModel {
    /**
     * The conversation name.
     */
    private final String conversationName;

    /**
     * The conversation type.
     */
    private final ConversationType conversationType;

    /**
     * A map for checking the last started conversation time with an npc.
     */
    private final transient Map<UUID, Long> lastStarted = new HashMap<>();

    /**
     * Creates a new conversation storage instance.
     *
     * @param conversationName The conversation name.
     */
    public ConversationModel(String conversationName,
                             String conversationType) {
        this.conversationName = conversationName;
        try {
            this.conversationType = ConversationType.valueOf(conversationType.toUpperCase());
        } catch (IllegalArgumentException exception) {
            throw new IllegalStateException("can't find conversation type " + conversationType);
        }
    }

    /**
     * Default no-args constructor, this would be used by gson
     * initializes default variables for missing fields since gson doesn't support it.
     */
    protected ConversationModel() {
        conversationName = null;
        conversationType = null;
    }

    /**
     * Returns the conversation name.
     *
     * @return The conversation name.
     */
    public String getConversationName() {
        return conversationName;
    }

    /**
     * Returns the conversation type.
     *
     * @return The conversation type.
     */
    public ConversationType getConversationType() {
        return conversationType;
    }

    /**
     * Returns the conversation.
     *
     * @return The conversation.
     */
    public Conversation getConversation() {
        return Conversation.forName(conversationName);
    }

    /**
     * Starts the conversation with the npc.
     *
     * @param znpc The npc to start the conversation with.
     * @param player The player that will start the conversation.
     */
    public void startConversation(ZNPC znpc,
                                  Player player) {
        if (!Conversation.exists(conversationName)) {
            throw new IllegalStateException("can't find conversation " + conversationName);
        }
        // check if player is currently conversing with an npc
        if (ConversationProcessor.isPlayerConversing(player.getUniqueId())) {
            // player already conversing with an npc return...
            return;
        }
        // check for last conversation time
        if (lastStarted.containsKey(player.getUniqueId())) {
            long lastConversationNanos = System.nanoTime() - lastStarted.get(player.getUniqueId());
            if (lastConversationNanos < Utils.SECOND_INTERVAL_NANOS * getConversation().getDelay()) {
                return;
            }
        }
        lastStarted.remove(player.getUniqueId());
        // conversation found, start
        if (conversationType.canStart(znpc, getConversation(), player)) {
            new ConversationProcessor(znpc, this, player);
            lastStarted.put(player.getUniqueId(), System.nanoTime());
        }
    }

    /**
     * Returns {@code true} if the player can start/continue a conversation.
     *
     * @param znpc The npc.
     * @param player The player
     * @return If the player can start/continue a conversation.
     */
    public boolean canRun(ZNPC znpc,
                          Player player) {
        return Stream.of(ConversationType.values())
                .anyMatch(conversationType1 -> !conversationType1.canStart(znpc, getConversation(), player));
    }

    /**
     * Determines how the conversation will be started for a player.
     */
    public enum ConversationType {
        /**
         * When the player is near an npc.
         */
        RADIUS {
            @Override
            public boolean canStart(ZNPC znpc,
                                    Conversation conversation,
                                    Player player) {
                return player.getWorld() == znpc.getLocation().getWorld()
                        && player.getLocation().distance(znpc.getLocation()) <= conversation.getRadius();
            }
        },
        /**
         * When the player clicks on an npc.
         */
        CLICK {
            @Override
            public boolean canStart(ZNPC znpc,
                                    Conversation conversation,
                                    Player player) {
                // continue on npc interact event
                return true;
            }
        };

        /**
         * Returns {@code true} if the player can start a conversation with an npc.
         *
         * @param znpc The npc to start the conversation with.
         * @param conversation The conversation.
         * @param player The player that will start the conversation.
         * @return If the player can start a conversation with an npc.
         */
        abstract boolean canStart(ZNPC znpc, Conversation conversation, Player player);
    }
}
