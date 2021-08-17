package ak.znetwork.znpcservers.user;

import ak.znetwork.znpcservers.ServersNPC;
import ak.znetwork.znpcservers.utility.SchedulerUtils;
import org.bukkit.event.Event;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * A build event service.
 * <p />
 * Create a new {@link EventService<T>} using the {@link #addService(ZUser, Class)} method.
 * <p>
 * <b>NOTE</b> For the service to be executed for a user you need to create a method with the {@link org.bukkit.event.Event} class type.
 * annotated with {@link org.bukkit.event.EventHandler}.
 * </p>
 * @param <T> The event class type.
 */
public class EventService<T extends Event> {
    /** The event class. */
    private final Class<T> eventClass;

    /** A list of consumers, these will be ran when the user triggers the event. */
    private final List<Consumer<T>> eventConsumers;

    /**
     * Creates a new {@link EventService}.
     *
     * @param eventClass The event class.
     * @param eventConsumers The event consumers.
     */
    protected EventService(Class<T> eventClass,
                           List<Consumer<T>> eventConsumers) {
        this.eventClass = eventClass;
        this.eventConsumers = eventConsumers;
    }

    /**
     * Returns the event class.
     */
    public Class<T> getEventClass() {
        return eventClass;
    }

    /**
     * Returns the event consumers.
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
     * Executes all the provided {@link #getEventConsumers()} for the instance event.
     *
     * @param event The event type {@link T}.
     */
    public void runAll(T event) {
        ServersNPC.SCHEDULER.runTask(() ->
                eventConsumers.forEach(consumer -> consumer.accept(event)));
    }

    /**
     * Registers a new {@link EventService} for the given user.
     *
     * @param user The user to add the service for.
     * @param eventClass The event class.
     * @param <T> The event type.
     * @throws IllegalStateException If the {@code user} already has a service for the given {@code eventServiceClass}.
     */
    public static <T extends Event> EventService<T> addService(ZUser user, Class<T> eventClass) {
        if (hasService(user, eventClass)) {
            throw new IllegalStateException(eventClass.getSimpleName() + " is already register for " + user.getUUID().toString());
        }
        EventService<T> service = new EventService<>(eventClass, new ArrayList<>());
        user.getEventServices().add(service);
        // close inventory for the user
        user.toPlayer().closeInventory();
        return service;
    }

    /**
     * Tries to find a event service for the given user,
     * if no service is found the method will return {@code null}.
     *
     * @param user The user to find the service for.
     * @param eventClass The event class.
     * @param <T> The event type.
     */
    @SuppressWarnings("unchecked")
    public static <T extends Event> EventService<T> findService(ZUser user, Class<T> eventClass) {
        return user.getEventServices().stream()
                .filter(eventService -> eventService.getEventClass().isAssignableFrom(eventClass))
                .map(EventService.class::cast)
                .findFirst()
                .orElse(null);
    }

    /**
     * Returns {@code true} if the user has a event service on the given {@code eventClass}.
     *
     * @param user The user.
     * @param eventClass The event class.
     * @return {@code true} the user has a event service for the given {@code eventClass}.
     */
    public static boolean hasService(ZUser user, Class<? extends Event> eventClass) {
        return user.getEventServices().stream().anyMatch(eventService -> eventService.getEventClass() == eventClass);
    }
}
