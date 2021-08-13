package ak.znetwork.znpcservers.npc.conversation;

import ak.znetwork.znpcservers.npc.NPCAction;
import com.google.common.base.Splitter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Key that identifies a text for a {@link Conversation},
 * {@link Conversation#getTexts()}.
 *
 * @see Conversation
 */
public class ConversationKey {
    private static final Splitter SPACE_SPLITTER = Splitter.on(" ");

    /**
     * The conversation text lines.
     */
    private final List<String> lines;

    /**
     * The conversation text actions.
     */
    private final List<NPCAction> actions;

    /**
     * The delay to wait to send the text lines. in seconds
     */
    private int delay = 1;

    /**
     * The sound to play when sending the text.
     */
    private String soundName;

    /**
     * Creates a new {@link ConversationKey}.
     *
     * @param line The conversation text line.
     */
    public ConversationKey(String line) {
        this(SPACE_SPLITTER.split(line));
    }

    /**
     * Creates a new {@link ConversationKey}.
     *
     * @param line The conversation text lines.
     */
    public ConversationKey(Iterable<String> line) {
        this.lines = StreamSupport.stream(line.spliterator(), false).map(String::toString).collect(Collectors.toList());
        actions = new ArrayList<>();
    }

    /**
     * Returns the key text lines.
     */
    public List<String> getLines() {
        return lines;
    }

    /**
     * Returns the delay to send the text lines.
     */
    public int getDelay() {
        return delay;
    }

    /**
     * Returns the sound name.
     */
    public String getSoundName() {
        return soundName;
    }

    /**
     * Returns the actions to run when sending the texts.
     */
    public List<NPCAction> getActions() {
        return actions;
    }

    /**
     * Sets the {@link #getDelay()} of this key.
     *
     * @param delay The new page.
     */
    public void setDelay(int delay) {
        this.delay = delay;
    }

    /**
     * Sets the {@link #getSoundName()} of this key.
     *
     * @param soundName The new sound name.
     */
    public void setSoundName(String soundName) {
        this.soundName = soundName;
    }

    /**
     * @inheritDoc
     */
    public String getTextFormatted() {
        if (lines.isEmpty()) {
            return "";
        }

        String text =
                lines.iterator().next();
        return text.substring(0, Math.min(text.length(), 24));
    }
}
