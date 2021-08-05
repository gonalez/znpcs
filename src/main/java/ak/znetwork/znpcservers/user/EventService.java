package ak.znetwork.znpcservers.user;

import org.bukkit.event.Event;

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
    private final Consumer<T> eventConsumer;

    /**
     * Creates a new service for the given event.
     *
     * @param eventClass The event class.
     * @param eventConsumer The event consumer.
     */
    public EventService(Class<T> eventClass,
                        Consumer<T> eventConsumer)  {
        this.eventClass = eventClass;
        this.eventConsumer = eventConsumer;
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
     * Returns the event consumer.
     *
     * @return The event consumer.
     */
    public Consumer<T> getEventConsumer() {
        return eventConsumer;
    }

    /**
     * Setups a new event service for the given player.
     *
     * @param zUser The user to add the service for.
     * @param eventClass The event class.
     * @param eventConsumer The consumer to run when the player runs the event.
     * @param <T> The event type.
     */
    public static <T extends Event> void addService(ZUser zUser,
                                             Class<T> eventClass,
                                             Consumer<T> eventConsumer) {
        if (hasService(zUser, eventClass)) {
            throw new IllegalStateException(eventClass.getSimpleName() + " is already register for " + zUser.getUuid().toString());
        }
        zUser.getEventServices().add(new EventService<>(eventClass, eventConsumer));
        zUser.toPlayer().closeInventory();
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
