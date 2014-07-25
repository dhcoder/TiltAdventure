package dhcoder.support.event;

import java.util.ArrayList;
import java.util.List;

/**
 * Class which encapsulates the concept of an event, by providing a method to fire the event and notifying a
 * collection of handlers when it is fired.
 */
public final class Event {

    private final List<EventHandler> listeners = new ArrayList<EventHandler>();

    /**
     * Add a handler to this event, a callback which will get triggered when the event happens.
     */
    public void addHandler(final EventHandler eventHandler) { listeners.add(eventHandler); }

    /**
     * Remove a handler added by {@link #addHandler(EventHandler)}.
     */
    public void removeHandler(final EventHandler eventHandler) { listeners.remove(eventHandler); }

    /**
     * Fire this event, triggering all listeners.
     */
    public void fire(final Object sender) {
        int listenerCount = listeners.size(); // Simple iteration to avoid Iterator allocation
        for (int i = 0; i < listenerCount; ++i) {
            listeners.get(i).run(sender);
        }
    }

    /**
     * Release all listening handlers.
     * <p/>
     * This is useful to do when the event is no longer used, as a listener it's holding on to may otherwise keep this
     * event reference alive longer than expected.
     */
    public void clear() { listeners.clear(); }
}
