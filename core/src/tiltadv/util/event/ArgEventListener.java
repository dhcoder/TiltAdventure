package tiltadv.util.event;

/**
 * An interface for registering {@link ArgEventHandler}s which will be triggered when an event happens.
 */
public interface ArgEventListener<T extends EventArgs> {

    /**
     * Add a listener to this event, a callback which will get triggered when the event happens.
     */
    void addListener(ArgEventHandler<T> listener);

    /**
     * Remove a listener added by {@link #addListener(ArgEventHandler)}
     */
    void removeListener(ArgEventHandler<T> listener);
}
