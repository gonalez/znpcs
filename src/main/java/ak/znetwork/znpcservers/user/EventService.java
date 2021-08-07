package ak.znetwork.znpcservers.user;

import com.google.common.collect.ImmutableList;
import org.bukkit.event.Event;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * <p>Copyright (c) ZNetwork, 2020.</p>
 *
 * @author ZNetwork
 * @since 3/8/2021
 */
@SuppressWarnings("unchecked")
public class EventService<T extends Event> {
    /**
     * The event class.
     */
    private final Class<T> eventClass;

    /**
     * The event consumer.
     */
    private final List<Consumer<T>> eventConsumers;

    /**
     * Creates a new service for the given event.
     *
     * @param eventClass The event class.
     * @param eventConsumers The event consumers.
     */
    protected EventService(Class<T> eventClass,
                        List<Consumer<T>> eventConsumers)  {
        this.eventClass = eventClass;
        this.eventConsumers = eventConsumers;
    }

    /**
     * Returns the event class.
     *
     * @return The event class.
     */
    public Class<T> getEventClass() {
        return eventClass;
    }

    /**
     * Returns the event consumers.
     *
     * @return The event consumers.
     */
    public List<Consumer<T>> getEventConsumers() {
        return eventConsumers;
    }

    /**
     * Adds a new operation consumer for the event service.
     *
     * @param consumer The operation to add.
     * @return The current event service.
     */
    public EventService<T> addConsumer(Consumer<T> consumer) {
        getEventConsumers().add(consumer);
        return this;
    }

    /**
     * Registers a new event service for the given player.
     *
     * @param zUser The user to add the service for.
     * @param eventServiceClass The event class.
     * @param <T> The event type.
     */
    public static <T extends Event> EventService<T> addService(ZUser zUser,
                                                    Class<T> eventServiceClass) {
        if (hasService(zUser, eventServiceClass)) {
            throw new IllegalStateException(eventServiceClass.getSimpleName() + " is already register for " + zUser.getUuid().toString());
        }
        EventService<T> service = new EventService<>(eventServiceClass, new ArrayList<>());
        zUser.getEventServices().add(service);
        // close inventory for player
        zUser.toPlayer().closeInventory();
        return service;
    }

    /**
     * Finds a event service for the given user.
     *
     * @param zUser The user to find the service for.
     * @param eventClass The event class.
     * @param <T> The event type.
     */
    public static <T extends Event> EventService<T> findService(ZUser zUser,
                                                                Class<T> eventClass) {
        return zUser.getEventServices().stream()
                .filter(eventService -> eventService.getEventClass().isAssignableFrom(eventClass))
                .map(EventService.class::cast)
                .findFirst()
                .orElse(null);
    }

    /**
     * Returns {@code true} if the user has a event service on the given event class.
     *
     * @param zUser The user.
     * @param eventClass The event class.
     * @return If the user has a event service on the given event class.
     */
    public static boolean hasService(ZUser zUser,
                                     Class<? extends Event> eventClass) {
        return zUser.getEventServices().stream()
                .anyMatch(eventService -> eventService.getEventClass() == eventClass);
    }
}
