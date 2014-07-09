package dhcoder.support.event;

/**
 * A wrapper around an {@link ArgEvent} which allows registering handlers but not firing the event.
 */
public final class ArgEventHandle<T extends EventArgs> {

    private final ArgEvent<T> argEvent;

    /**
     * Package-private constructor. Use {@link ArgEvent#asHandle} instead!
     */
    ArgEventHandle(final ArgEvent<T> argEvent) {
        this.argEvent = argEvent;
    }

    /**
     * Add a handler to this event, a callback which will get triggered when the event happens.
     */
    public void addHandler(final ArgEventHandler<T> eventHandler) {
        argEvent.addHandler(eventHandler);
    }

    /**
     * Remove a handler added by {@link #addHandler(ArgEventHandler)}
     */
    public void removeHandler(final ArgEventHandler<T> eventHandler) {
        argEvent.addHandler(eventHandler);

    }
}
