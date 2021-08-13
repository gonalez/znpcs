package ak.znetwork.znpcservers.npc.hologram.replacer;

import ak.znetwork.znpcservers.configuration.ConfigTypes;
import net.md_5.bungee.api.ChatColor;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Enables RGB for the string.
 */
public class RGBLine implements LineReplacer<String> {
    /**
     * Default hex-color-code length.
     */
    private static final int HEX_COLOR_LENGTH = 6;

    @Override
    public String make(String string) {
        String rgbString = string;
        for (int i = 0; i < rgbString.length(); i++) {
            char charAt = rgbString.charAt(i);
            // check if the char is supposed to be a hex color code
            if (charAt == '#') {
                int endIndex = i+HEX_COLOR_LENGTH+1;
                boolean success = true;
                StringBuilder hexCodeStringBuilder = new StringBuilder();
                for (int i2 = i; i2 < endIndex; i2++) {
                    // check if string length is in range (hex-default length)
                    if (rgbString.length() - 1 < i2) {
                        success = false;
                        break;
                    }
                    char hexCode = rgbString.charAt(i2);
                    hexCodeStringBuilder.append(ConfigTypes.RGB_ANIMATION
                            && hexCode != '#' ? Integer.toHexString(ThreadLocalRandom.current().nextInt(0xf+1)) : hexCode);
                }
                // found RGB Color!
                if (success) {
                    try {
                        // apply RGB Color to string..
                        rgbString = rgbString.substring(0, i) +
                                ChatColor.of(hexCodeStringBuilder.toString()) +
                                rgbString.substring(endIndex);
                    } catch (Exception e) {
                        // invalid hex string
                    }
                }
            }
        }
        return rgbString;
    }
}
