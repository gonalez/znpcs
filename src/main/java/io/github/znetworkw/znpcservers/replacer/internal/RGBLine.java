package io.github.znetworkw.znpcservers.replacer.internal;

import io.github.znetworkw.znpcservers.replacer.StringReplacer;
import io.github.znetworkw.znpcservers.utility.Utils;
import net.md_5.bungee.api.ChatColor;

/**
 * Enables RGB support for strings.
 *
 * @author Gaston Gonzalez {@literal <znetworkw.dev@gmail.com>}
 */
public class RGBLine implements StringReplacer {
    /**
     * The hex color symbol {@code '#'} must be
     * followed by {@code MAX_HEX_LENGTH} characters.
     */
    private static final char HEX_SYMBOL = '#';

    /**
     * The max length for hex color ({@code 6}).
     */
    private static final int MAX_HEX_LENGTH = 6;

    @Override
    public String apply(String string) {
        String rgbString = string;
        for (int i = 0; i < rgbString.length(); i++) {
            if (rgbString.charAt(i) == HEX_SYMBOL) { // check if the char is hex
                final int endIndex = i + MAX_HEX_LENGTH + 1;
                final StringBuilder stringBuilder = new StringBuilder();
                boolean success = true;
                for (int i2 = i; i2 < endIndex; i2++) {
                    if (rgbString.length() - 1 < i2) { // check if string length is in range (#MAX_HEX_LENGTH)
                        success = false;
                        break;
                    }
                    stringBuilder.append(rgbString.charAt(i2));
                }
                if (success) { // found hex color
                    try {
                        // apply hex color to the string
                        rgbString = rgbString.substring(0, i)
                            + ChatColor.of(stringBuilder.toString())
                            + rgbString.substring(endIndex);
                    } catch (Exception e) {
                        // invalid hex string... (skip)
                    }
                }
            }
        }
        return rgbString;
    }

    @Override
    public boolean isSupported() {
        return Utils.BUKKIT_VERSION > 15;
    }
}
