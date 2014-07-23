package dhcoder.support.state;

import dhcoder.support.opt.Opt;

import java.util.HashMap;
import java.util.Map;

import static dhcoder.support.utils.StringUtils.format;
import static dhcoder.support.opt.Opt.of;

/**
 * Encapsulation of a finite state machine.
 * <p/>
 * You instantiate a state machine by registering a list of states and a list of events that it can accept in each
 * state.
 *
 * @param <S> An enumeration type that represents the known states this machine can get into.
 * @param <E> An enumeration type that represents the known events this machine can accept.
 */
public abstract class StateMachine<S extends Enum, E extends Enum> {

    private class StateEvent {

        private final S state;
        private final E event;

        private StateEvent(final S state, final E event) {
            this.state = state;
            this.event = event;
        }

        @Override
        public int hashCode() {
            int result = state.hashCode();
            result = 31 * result + event.hashCode();
            return result;
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) { return true; }
            if (o == null || getClass() != o.getClass()) { return false; }

            StateEvent that = (StateEvent)o;

            if (!event.equals(that.event)) { return false; }
            if (!state.equals(that.state)) { return false; }

            return true;
        }
    }

    private final Map<StateEvent, StateTransitionHandler<S, E>> eventResponses =
        new HashMap<StateEvent, StateTransitionHandler<S, E>>();
    private final Opt<StateEventHandler<S, E>> defaultHandlerOpt = Opt.withNoValue();
    private S currentState;

    public StateMachine(final S startState) {
        currentState = startState;
    }

    public S getCurrentState() {
        return currentState;
    }

    /**
     * Set a method handler which, if set, will get called any time an event is called on the state machine that isn't
     * handled.
     */
    public void setDefaultHandler(final StateEventHandler<S, E> defaultHandler) {
        defaultHandlerOpt.set(defaultHandler);
    }

    /**
     * Register a state/event pair with a handler that will get triggered if the event happens when the state is active.
     * <p/>
     * It is an error to register more than one handler for any state/event pair.
     *
     * @throws IllegalArgumentException if the state/event pair has previously been registered.
     */
    public void registerEvent(final S state, final E event, final StateTransitionHandler<S, E> eventHandler) {
        StateEvent pair = new StateEvent(state, event);

        if (eventResponses.containsKey(pair)) {
            throw new IllegalArgumentException(
                format("Duplicate registration of state+event pair: {0}, {1}.", state, event));
        }

        eventResponses.put(pair, eventHandler);
    }

    /**
     * Tell the state machine to handle the passed in event given the current state.
     */
    public void handleEvent(final E event) {
        handleEvent(event, Opt.withNoValue());
    }

    /**
     * Like {@link #handleEvent(Enum)} but with some additional data that is related to the event.
     */
    public void handleEvent(final E event, final Object eventData) {
        handleEvent(event, of(eventData));
    }

    private void handleEvent(final E event, final Opt eventData) {
        StateEvent pair = new StateEvent(currentState, event);
        if (!eventResponses.containsKey(pair)) {
            if (defaultHandlerOpt.hasValue()) {
                defaultHandlerOpt.getValue().run(currentState, event, eventData);
            }
            return;
        }

        StateTransitionHandler<S, E> eventHandler = eventResponses.get(pair);
        Opt<S> newStateOpt = eventHandler.run(currentState, event, eventData);

        if (newStateOpt.hasValue()) {
            currentState = newStateOpt.getValue();
        }
    }
}

