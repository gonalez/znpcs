package ak.znetwork.znpcservers.npc.conversation;

import ak.znetwork.znpcservers.ServersNPC;
import ak.znetwork.znpcservers.hologram.replacer.LineReplacer;
import ak.znetwork.znpcservers.npc.ZNPC;
import ak.znetwork.znpcservers.types.ConfigTypes;
import ak.znetwork.znpcservers.user.ZUser;
import ak.znetwork.znpcservers.utility.Utils;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * <p>Copyright (c) ZNetwork, 2020.</p>
 *
 * @author ZNetwork
 * @since 4/8/2021
 */
public class ConversationProcessor {
    /**
     * A map that contains the players that are running a conversations with an npc.
     */
    private static final Map<UUID, String> RUNNING_CONVERSATIONS = new HashMap<>();

    /**
     * A string whitespace;
     */
    private static final String WHITE_SPACE = " ";

    /**
     * The delay to run new conversation key.
     */
    private static final int CONVERSATION_DELAY = 20; // 1 second

    /**
     * The npc that the player started the conversation with.
     */
    private final ZNPC znpc;

    /**
     * The conversation.
     */
    private final ConversationModel conversationStorage;

    /**
     * The player that started the conversation.
     */
    private final Player player;

    /**
     * The current conversation key index.
     */
    private int conversationIndex = 0;

    /**
     * Last key delay.
     */
    private long conversationIndexDelay = System.nanoTime();

    /**
     * Creates a new conversation handler for an player.
     *
     * @param conversationStorage The conversation.
     * @param znpc                The npc that the player started the conversation with.
     * @param player              The player that started the conversation.
     */
    public ConversationProcessor(ZNPC znpc,
                                 ConversationModel conversationStorage,
                                 Player player) {
        if (conversationStorage.getConversation().getTexts().isEmpty()) {
            throw new IllegalStateException("conversation should have a text");
        }
        this.znpc = znpc;
        this.conversationStorage = conversationStorage;
        this.player = player;
        RUNNING_CONVERSATIONS.put(player.getUniqueId(), conversationStorage.getConversationName());
        start();
    }

    /**
     * Starts the conversation with the npc.
     */
    public void start() {
        ServersNPC.SCHEDULER.runTaskTimer(new BukkitRunnable() {
            @Override
            public void run() {
                // basic conversation checks
                if (Bukkit.getPlayer(player.getUniqueId()) == null ||
                        conversationIndex > conversationStorage.getConversation().getTexts().size() - 1 ||
                        conversationStorage.canRun(znpc, player)) {
                    // conversation end
                    RUNNING_CONVERSATIONS.remove(player.getUniqueId());
                    cancel();
                    return;
                }
                ConversationKey conversationKey = conversationStorage.getConversation().getTexts().get(conversationIndex);
                // check for delay
                long conversationDelayNanos = System.nanoTime() - conversationIndexDelay;
                if (conversationIndex != 0 && conversationDelayNanos < Utils.SECOND_INTERVAL_NANOS * conversationKey.getDelay()) {
                    return;
                }
                // send text to player
                conversationKey.getLines().forEach(s -> player.sendMessage(LineReplacer.makeAll(player, s).replace(ConfigTypes.SPACE_SYMBOL, WHITE_SPACE)));
                // check for conversation actions
                if (conversationKey.getActions().size() > 0) {
                    final ZUser user = ZUser.find(player);
                    conversationKey.getActions().forEach(action -> action.run(user, action.getAction()));
                }
                // send sound
                if (conversationKey.getSoundName() != null &&
                        conversationKey.getSoundName().length() > 0) {
                    try {
                        Sound sound = Sound.valueOf(conversationKey.getSoundName().toUpperCase());
                        player.playSound(player.getLocation(), sound, 0.2f, 1.0f);
                    } catch (IllegalArgumentException e) {
                        // skip sound
                    }
                }
                // update times
                conversationIndexDelay = System.nanoTime();
                conversationIndex++;
            }
        }, 5, CONVERSATION_DELAY);
    }

    /**
     * Returns {@code true} if the player is conversing with an npc.
     *
     * @param uuid The player uuid.
     * @return If the player is conversing with an npc.
     */
    public static boolean isPlayerConversing(UUID uuid) {
        return RUNNING_CONVERSATIONS.containsKey(uuid);
    }
}
