package io.github.znetworkw.znpcservers.npc.conversation;

import io.github.znetworkw.znpcservers.npc.NPC;
import io.github.znetworkw.znpcservers.utility.Utils;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Stream;

/**
 * A struct data model for a {@link Conversation}.
 *
 * <p>
 * To start a new conversation with a {@link Player} use {@link #startConversation(NPC, Player)}.
 * <p />
 * <b>NOTE:</b> the conversation must have 1 or more texts ({@link Conversation#getTexts()}).
 *
 * @see Conversation
 */
public class ConversationModel {
    /**
     * The conversation name.
     */
    private String conversationName;

    /**
     * The conversation type.
     */
    private ConversationType conversationType;

    /**
     * A map for checking the last started conversation time with an npc for a player.
     */
    private final transient Map<UUID, Long> lastStarted = new HashMap<>();

    /**
     * Creates a new {@link ConversationModel}.
     *
     * @param conversationName The conversation name.
     * @throws IllegalStateException If cannot find conversation type.
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
     * Starts the conversation with the given npc.
     *
     * @param npc The npc to start the conversation with.
     * @param player The player that will start the conversation.
     * @throws IllegalStateException If cannot find conversation.
     */
    public void startConversation(NPC npc,
                                  Player player) {
        if (!Conversation.exists(conversationName)) {
            throw new IllegalStateException("can't find conversation " + conversationName);
        }
        if (ConversationProcessor.isPlayerConversing(player.getUniqueId())) { // check if the player is currently conversing with an npc
            return;
        }
        // check for the last player conversation time
        if (lastStarted.containsKey(player.getUniqueId())) {
            long lastConversationNanos = System.nanoTime() - lastStarted.get(player.getUniqueId());
            if (lastConversationNanos < Utils.SECOND_INTERVAL_NANOS * getConversation().getDelay()) {
                return;
            }
        }
        lastStarted.remove(player.getUniqueId());
        if (conversationType.canStart(npc, getConversation(), player)) {  // conversation found, start
            new ConversationProcessor(npc, this, player);
            lastStarted.put(player.getUniqueId(), System.nanoTime());
        }
    }

    /**
     * Returns {@code true} if the player can start/continue with the conversation.
     *
     * @param npc The npc.
     * @param player The player
     * @return {@code true} If the player can start/continue with the conversation.
     */
    public boolean canRun(NPC npc, Player player) {
        return Stream.of(ConversationType.values()).anyMatch(conversationType1 -> !conversationType1.canStart(npc, getConversation(), player));
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
            public boolean canStart(NPC npc,
                                    Conversation conversation,
                                    Player player) {
                return player.getWorld() == npc.getLocation().getWorld()
                    && player.getLocation().distance(npc.getLocation()) <= conversation.getRadius();
            }
        },
        /**
         * When the player clicks on an npc.
         */
        CLICK {
            @Override
            public boolean canStart(NPC npc,
                                    Conversation conversation,
                                    Player player) {
                // continue on npc interact event
                return true;
            }
        };

        /**
         * Returns {@code true} if the player can start a conversation with given npc.
         *
         * @param npc The npc to start the conversation with.
         * @param conversation The conversation.
         * @param player The player that will start the conversation.
         * @return If the player can start a conversation with the npc.
         */
        abstract boolean canStart(NPC npc, Conversation conversation, Player player);
    }

    /** Required by gson */
    private ConversationModel() {}
}
