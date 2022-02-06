package io.github.gonalez.znpcservers.user;

/**
 * Helper methods for users.
 *
 * @author Gaston Gonzalez {@literal <znetworkw.dev@gmail.com>}
 */
public final class UserUtils {
    /**
     *
     * @param users
     * @param packets
     */
    public static void sendPackets(Iterable<User> users, Object... packets) {
        for (User user : users) {
            for (Object packet : packets) {
                try {
                    user.sendPackets(packet);
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }
        }
    }

    private UserUtils() {}
}
