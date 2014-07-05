package tiltadv.util.event;

/**
 * A class which can register listeners which are triggered on an event happening.
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
