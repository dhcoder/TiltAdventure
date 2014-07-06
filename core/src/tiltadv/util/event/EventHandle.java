package tiltadv.util.event;

/**
 * A wrapper around an {@link Event} which allows registering handlers but not firing the event.
 */
public final class EventHandle {

    private final Event event;

    /**
     * Package-private constructor. Use {@link Event#asHandle} instead!
     */
    EventHandle(final Event event) {
        this.event = event;
    }

    /**
     * Add a handler to this event, a callback which will get triggered when the event happens.
     */
    public void addHandler(final EventHandler eventHandler) {
        event.addHandler(eventHandler);
    }

    /**
     * Remove a handler added by {@link #addHandler(EventHandler)}.
     */
    public void removeHandler(final EventHandler eventHandler) {
        event.removeHandler(eventHandler);
    }
}
