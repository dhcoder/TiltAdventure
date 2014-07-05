package tiltadv.util.event;

/**
 * An interface for registering {@link EventHandler}s which will be triggered when an event happens.
 */
public interface EventListener {

    /**
     * Add a listener to this event, a callback which will get triggered when the event happens.
     */
    void addListener(EventHandler listener);

    /**
     * Remove a listener added by {@link #addListener(EventHandler)}.
     */
    void removeListener(EventHandler listener);
}
