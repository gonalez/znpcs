package io.github.gonalez.znpcservers.user;

import io.github.gonalez.znpcservers.user.internal.DefaultUserStore;

import java.util.UUID;

/**
 * Interface for storing and retrieving {@link User}s.
 *
 * @author Gaston Gonzalez {@literal <znetworkw.dev@gmail.com>}
 */
public interface UserStore {
    /**
     * Creates a new, default user store.
     *
     * @return a new user store.
     */
    static UserStore of() {
        return new DefaultUserStore();
    }

    /**
     * Retrieves the user with the given uuid.
     *
     * @param uuid the user uuid.
     * @return
     *    the user matching the given uuid, or
     *    {@code null} if no match user was found.
     */
    User getUser(UUID uuid);

    /**
     * Adds a new user to this storage, typically the uuid used
     * for getting the user when using the {@link #getUser(UUID)}
     * method will be the {@link User#getUUID() user uuid}.
     *
     * @param user the user to add.
     * @return the new added user.
     */
    User addUser(User user);

    /**
     * Removes the user matching the given uuid from the storage.
     *
     * @param uuid the user uuid.
     */
    void removeUser(UUID uuid);

    /**
     * Retrieves all users in this storage, the order of iteration is undefined.
     *
     * @return all users in this storage.
     */
    Iterable<User> getUsers();
}
