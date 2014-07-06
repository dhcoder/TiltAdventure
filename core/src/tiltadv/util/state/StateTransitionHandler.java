package tiltadv.util.state;

import tiltadv.util.Opt;
import tiltadv.util.lambda.Func3;

/**
 * A method which handles a state machine's event transition, returning which state the machine should transition into.
 *
 * @param <S> An enumeration type that represents the known states this machine can get into.
 */
public interface StateTransitionHandler<S extends Enum, E extends Enum> extends Func3<Opt<S>, S, E, Opt> {

    @Override
    Opt<S> run(S fromState, E withEvent, Opt eventData);
}
