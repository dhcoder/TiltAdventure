package tiltadv.util.event;

import tiltadv.util.lambda.Action1;

import java.util.ArrayList;
import java.util.List;

/**
 * An implementation of {@link EventListener} which provides a method to fire it. A class should use this event
 * internally but expose it externally via its listener interface.
 */
public class Event implements EventListener {

    private final List<EventHandler> listeners = new ArrayList<EventHandler>();

    @Override
    public void addListener(final EventHandler listener) {
        listeners.add(listener);
    }

    @Override
    public void removeListener(final EventHandler listener) {
        listeners.remove(listener);
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
     *
     * This is useful to do when the event is no longer used, as a listener it's holding on to may otherwise keep it
     * alive longer than expected.
     */
    public void clear() {
        listeners.clear();
    }
}
