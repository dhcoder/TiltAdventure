package d9n.utils.event;

import java.util.ArrayList;
import java.util.List;

/**
 * Class which encapsulates the concept of an event, by providing a method to fire the event and notifying a
 * collection of handlers when it is fired.
 * <p/>
 * A class should use Event internally but expose it externally via a {@link EventHandle}
 */
public class Event {

    private final List<EventHandler> listeners = new ArrayList<EventHandler>();
    private final EventHandle eventHandle;

    public Event() {
        eventHandle = new EventHandle(this);
    }

    /**
     * Add a handler to this event, a callback which will get triggered when the event happens.
     */
    public void addHandler(final EventHandler eventHandler) {
        listeners.add(eventHandler);
    }

    /**
     * Remove a handler added by {@link #addHandler(EventHandler)}.
     */
    public void removeHandler(final EventHandler eventHandler) {
        listeners.remove(eventHandler);
    }

    /**
     * Fire this event, triggering all listeners.
     */
    public void fire(final Object sender) {
        for (EventHandler listener : listeners) {
            listener.run(sender);
        }
    }

    /**
     * Release all listening handlers.
     * <p/>
     * This is useful to do when the event is no longer used, as a listener it's holding on to may otherwise keep it
     * alive longer than expected.
     */
    public void clear() {
        listeners.clear();
    }

    /**
     * Return a handle to this event which is safe to expose to external classes.
     */
    public EventHandle asHandle() {
        return eventHandle;
    }
}
