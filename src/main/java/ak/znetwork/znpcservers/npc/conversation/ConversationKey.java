package ak.znetwork.znpcservers.npc.conversation;

import ak.znetwork.znpcservers.npc.ZNPCAction;
import com.google.common.base.Splitter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * <p>Copyright (c) ZNetwork, 2020.</p>
 *
 * @author ZNetwork
 * @since 2/8/2021
 */
public class ConversationKey {
    /**
     * Splitter.
     */
    private static final Splitter SPACE_SPLITTER = Splitter.on(" ");

    /**
     * The conversation text lines.
     */
    private final List<String> lines;

    /**
     * The conversation text actions.
     */
    private final List<ZNPCAction> actions;

    /**
     * The seconds to wait to send the text message.
     */
    private int delay = 1;

    /**
     * The sound to play when sending the text.
     */
    private String soundName;

    /**
     * Creates a new conversation line.
     *
     * @param line The conversation text line.
     */
    public ConversationKey(String line) {
        this(SPACE_SPLITTER.split(line));
    }

    /**
     * Creates a new conversation line.
     *
     * @param line The conversation text line.
     */
    public ConversationKey(Iterable<String> line) {
        this.lines = StreamSupport.stream(line.spliterator(), false).map(String::toString).collect(Collectors.toList());
        actions = new ArrayList<>();
    }

    /**
     * The key lines.
     *
     * @return The key lines.
     */
    public List<String> getLines() {
        return lines;
    }

    /**
     * The delay to send the lines.
     *
     * @return The delay.
     */
    public int getDelay() {
        return delay;
    }

    /**
     * Returns the sound name.
     *
     * @return The sound name.
     */
    public String getSoundName() {
        return soundName;
    }

    /**
     * Returns the actions.
     *
     * @return The actions list.
     */
    public List<ZNPCAction> getActions() {
        return actions;
    }

    /**
     * Sets the delay to send the message.
     *
     * @param delay The new delay.
     */
    public void setDelay(int delay) {
        this.delay = delay;
    }

    /**
     * Sets the sound name.
     *
     * @param soundName The sound name.
     */
    public void setSoundName(String soundName) {
        this.soundName = soundName;
    }

    /**
     * Returns the first text in the list.
     *
     * @return The first text in the list
     */
    public String getFirstTextFormatted() {
        if (!lines.isEmpty()) {
            String firstText = lines.get(0);
            return firstText.substring(0, Math.min(firstText.length(), 24));
        }
        return "???";
    }
}
