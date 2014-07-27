package dhcoder.support.event;

import java.util.ArrayList;

/**
 * Like {@link Event} but includes additional {@link EventArgs} when fired.
 */
public final class ArgEvent<T extends EventArgs> {

    private final ArrayList<ArgEventHandler<T>> listeners = new ArrayList<ArgEventHandler<T>>();

    public void addHandler(final ArgEventHandler<T> listener) {
        listeners.add(listener);
    }

    public void removeHandler(final ArgEventHandler<T> listener) {
        listeners.remove(listener);
    }

    /**
     * Fire this event, triggering all listeners.
     */
    public void fire(final Object sender, final T args) {
        for (ArgEventHandler<T> listener : listeners) {
            listener.run(sender, args);
        }
    }

    /**
     * Release all listening handlers.
     * <p/>
     * This is useful to do when the event is no longer used, as a listener it's holding on to may otherwise keep it
     * alive longer than expected.
     */
    public void clearListeners() {
        listeners.clear();
    }
}
