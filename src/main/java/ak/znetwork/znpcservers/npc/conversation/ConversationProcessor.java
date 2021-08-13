package ak.znetwork.znpcservers.npc.conversation;

import ak.znetwork.znpcservers.ServersNPC;
import ak.znetwork.znpcservers.npc.hologram.replacer.LineReplacer;
import ak.znetwork.znpcservers.npc.NPC;
import ak.znetwork.znpcservers.configuration.ConfigTypes;
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
 * Handles a conversation for a {@link ConversationModel}.
 *
 * <p>
 * <b>NOTE:</b> Don't use this directly, use {@link ConversationModel#startConversation(NPC, Player)} instead.
 *
 * @see ConversationModel
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
    private final NPC npc;

    /**
     * The conversation.
     */
    private final ConversationModel conversationModel;

    /**
     * The player that started the conversation.
     */
    private final Player player;

    /**
     * The current conversation key index.
     */
    private int conversationIndex = 0;

    /**
     * Last send delay.
     */
    private long conversationIndexDelay = System.nanoTime();

    /**
     * Creates a new {@link ConversationProcessor} for a player.
     *
     * @param conversationModel The conversation.
     * @param npc The npc that the player started the conversation with.
     * @param player The player that started the conversation.
     * @throws IllegalStateException If the conversation have no text.
     */
    public ConversationProcessor(NPC npc,
                                 ConversationModel conversationModel,
                                 Player player) {
        if (conversationModel.getConversation().getTexts().isEmpty()) {
            throw new IllegalStateException("conversation should have a text.");
        }
        this.npc = npc;
        this.conversationModel = conversationModel;
        this.player = player;
        RUNNING_CONVERSATIONS.put(player.getUniqueId(), conversationModel.getConversationName());
        start();
    }

    /**
     * Starts the conversation task with the npc. The task will stop when the user disconnects
     * or the current {@code conversationIndex} is greater than the size of the conversation texts.
     */
    private void start() {
        ServersNPC.SCHEDULER.runTaskTimer(new BukkitRunnable() {
            @Override
            public void run() {
                // basic conversation checks
                if (Bukkit.getPlayer(player.getUniqueId()) == null ||
                        conversationIndex > conversationModel.getConversation().getTexts().size() - 1 ||
                        conversationModel.canRun(npc, player)) {
                    // conversation end
                    RUNNING_CONVERSATIONS.remove(player.getUniqueId());
                    cancel();
                    return;
                }
                ConversationKey conversationKey = conversationModel.getConversation().getTexts().get(conversationIndex);
                // check for delay
                long conversationDelayNanos = System.nanoTime() - conversationIndexDelay;
                if (conversationIndex != 0 && conversationDelayNanos < Utils.SECOND_INTERVAL_NANOS * conversationKey.getDelay()) {
                    return;
                }
                final ZUser user = ZUser.find(player);
                // send text to player
                conversationKey.getLines().forEach(s -> player.sendMessage(LineReplacer.makeAll(user, s).replace(ConfigTypes.SPACE_SYMBOL, WHITE_SPACE)));
                if (conversationKey.getActions().size() > 0) { // check for conversation actions
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
     * Returns {@code true} if there is a player with the given {@code uuid} conversing with an npc.
     *
     * @param uuid The uuid.
     * @return If the player is conversing with an npc.
     */
    public static boolean isPlayerConversing(UUID uuid) {
        return RUNNING_CONVERSATIONS.containsKey(uuid);
    }
}
