package ak.znetwork.znpcservers.utility;

import java.util.concurrent.ThreadLocalRandom;

/**
 * <p>Copyright (c) ZNetwork, 2020.</p>
 *
 * @author ZNetwork
 * @since 07/02/2020
 */
public class RandomString {
    /**
     * The random string length.
     */
    private final int length;

    /**
     * Creates a random {@link java.lang.String} instance with the given length.
     *
     * @param length The random {@link java.lang.String} length.
     */
    public RandomString(int length) {
        this.length = length;
    }

    /**
     * Creates a random {@link java.lang.String} with the specified character {@code length}.
     *
     * @return A random {@link java.lang.String}.
     */
    public String create() {
        StringBuilder stringBuilder = new StringBuilder();
        for (int index = 0; index < length; index++) {
            stringBuilder.append(ThreadLocalRandom.current().nextInt(0, 9));
        }
        return stringBuilder.toString();
    }
}
