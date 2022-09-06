package io.github.gonalez.znpcs.user.internal;

import io.github.gonalez.znpcs.user.User;
import io.github.gonalez.znpcs.user.UserStore;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A simple, default implementation of {@link UserStore}.
 *
 * @author Gaston Gonzalez {@literal <znetworkw.dev@gmail.com>}
 */
public class DefaultUserStore implements UserStore {
    private final Map<UUID, User> userMap = new ConcurrentHashMap<>();

    @Override
    public User getUser(UUID uuid) {
        return userMap.get(uuid);
    }

    @Override
    public User addUser(User user) {
        return userMap.computeIfAbsent(user.getUUID(), (u) -> user);
    }

    @Override
    public void removeUser(UUID uuid) {
        userMap.remove(uuid);
    }

    @Override
    public Iterable<User> getUsers() {
        return userMap.values();
    }
}
