package tiltadv.util.state;

import tiltadv.util.opt.Opt;
import tiltadv.util.lambda.Action3;

/**
 * A method like {@link StateTransitionHandler} but doesn't return a state to transition into.
 *
 * @param <S> An enumeration type that represents the known states this machine can get into.
 * @param <E> An enumeration type that represents the known events this machine can accept.
 */
public interface StateEventHandler<S extends Enum, E extends Enum> extends Action3<S, E, Opt> {

    @Override
    void run(S fromState, E withEvent, Opt eventData);
}
