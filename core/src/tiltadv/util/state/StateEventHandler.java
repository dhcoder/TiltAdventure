package tiltadv.util.state;

import tiltadv.util.opt.Opt;
import tiltadv.util.lambda.Action3;

/**
 * A method like {@link StateTransitionHandler} but doesn't return a state to transition into.
 */
public interface StateEventHandler<S extends Enum, E extends Enum> extends Action3<S, E, Opt> {

    @Override
    void run(S fromState, E withEvent, Opt eventData);
}
