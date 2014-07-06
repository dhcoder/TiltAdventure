package d9n.utils.state;

import d9n.utils.opt.Opt;
import d9n.utils.lambda.Func3;

/**
 * A method which handles a state machine's event transition, returning which state the machine should transition into.
 *
 * @param <S> An enumeration type that represents the known states this machine can get into.
 * @param <E> An enumeration type that represents the known events this machine can accept.
 */
public interface StateTransitionHandler<S extends Enum, E extends Enum> extends Func3<Opt<S>, S, E, Opt> {

    @Override
    Opt<S> run(S fromState, E withEvent, Opt eventData);
}
