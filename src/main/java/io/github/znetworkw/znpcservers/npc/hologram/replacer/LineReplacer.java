package io.github.znetworkw.znpcservers.npc.hologram.replacer;

import com.google.common.collect.ImmutableList;
import io.github.znetworkw.znpcservers.user.ZUser;
import io.github.znetworkw.znpcservers.utility.Utils;

/**
 * Interface used for replacing a {@link java.lang.String}.
 */
public interface LineReplacer {
    /** List of allowed custom line replacers. */
    ImmutableList<LineReplacer> LINE_REPLACERS =
        ImmutableList.of(new RGBLine());

    /**
     * Replaces the given string.
     *
     * @param string The string to replace.
     * @return The converted string.
     */
    String make(String string);

    /**
     * Returns {@code true} if should replace the line.
     *
     * @return {@code true} If should replace the line.
     */
    boolean isSupported();

    /**
     * Replaces the given {@link java.lang.String} with custom replaces.
     *
     * @param user The player to get placeholders for.
     * @param string The string to convert.
     * @return The converted string.
     */
    static String makeAll(ZUser user,
                          String string) {
        for (LineReplacer lineReplacer : LINE_REPLACERS) {
            if (!lineReplacer.isSupported()) {
                continue;
            }
            string = lineReplacer.make(string);
        }
        return Utils.toColor(Utils.PLACEHOLDER_SUPPORT && user != null ?
                Utils.getWithPlaceholders(string, user.toPlayer()) :
                string);
    }
}
