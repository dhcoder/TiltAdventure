package tiltadv.components.behavior;

import com.badlogic.gdx.math.Vector2;
import dhcoder.libgdx.entity.AbstractComponent;
import dhcoder.libgdx.entity.Entity;
import dhcoder.support.math.CardinalDirection;
import dhcoder.support.opt.Opt;
import dhcoder.support.state.StateMachine;
import dhcoder.support.state.StateTransitionHandler;
import dhcoder.support.time.Duration;
import tiltadv.components.model.HeadingComponent;
import tiltadv.components.model.MotionComponent;
import tiltadv.memory.Pools;

import java.util.Random;

/**
 * Component that maintains the state and logic of the octo enemy.
 */
public final class OctoBehaviorComponent extends AbstractComponent {

    private enum State {
        WAITING,
        MOVING,
        STOPPING,
    }

    private enum Evt {
        MOVE,
        WAIT,
    }

    private static final float SPEED = 30.0f;
    private static final Duration STOPPING_DURATION = Duration.fromSeconds(0.3f);
    private static final Random random = new Random();
    private final Duration remainingDuration = Duration.zero();
    private final StateMachine<State, Evt> octoState;
    private Evt followupEvent;

    private HeadingComponent headingComponent;
    private MotionComponent motionComponent;

    public OctoBehaviorComponent() {
        octoState = createStateMachine();
        resetComponent();
    }

    @Override
    public void initialize(final Entity owner) {
        headingComponent = owner.requireComponent(HeadingComponent.class);
        motionComponent = owner.requireComponent(MotionComponent.class);
    }

    @Override
    public void update(final Duration elapsedTime) {
        if (remainingDuration.getSeconds() >= elapsedTime.getSeconds()) {
            remainingDuration.subtract(elapsedTime);
        }
        else {
            remainingDuration.setSeconds(0.5f + random.nextFloat() * 3.0f);
            octoState.handleEvent(followupEvent);
        }
    }

    @Override
    protected void resetComponent() {
        octoState.reset();
        followupEvent = Evt.MOVE;
        remainingDuration.setZero();

        headingComponent = null;
        motionComponent = null;
    }

    private StateMachine<State, Evt> createStateMachine() {
        StateMachine<State, Evt> fsm = new StateMachine<State, Evt>(State.WAITING);

        fsm.registerEvent(State.WAITING, Evt.MOVE, new StateTransitionHandler<State, Evt>() {
            @Override
            public State run(final State fromState, final Evt withEvent, final Opt eventData) {

                Vector2 velocity = Pools.vector2s.grabNew();
                CardinalDirection nextDirection = CardinalDirection.getRandom();
                headingComponent.setHeading(nextDirection.getAngle());
                switch (nextDirection) {
                    case N:
                        velocity.set(0f, SPEED);
                        break;
                    case S:
                        velocity.set(0f, -SPEED);
                        break;
                    case E:
                        velocity.set(SPEED, 0f);
                        break;
                    case W:
                        velocity.set(-SPEED, 0f);
                        break;
                }
                motionComponent.setVelocity(velocity);
                Pools.vector2s.free(velocity);
                followupEvent = Evt.WAIT;
                return State.MOVING;
            }
        });

        fsm.registerEvent(State.MOVING, Evt.WAIT, new StateTransitionHandler<State, Evt>() {
            @Override
            public State run(final State fromState, final Evt withEvent, final Opt eventData) {
                motionComponent.stopSmoothly(STOPPING_DURATION);
                remainingDuration.setFrom(STOPPING_DURATION);
                return State.STOPPING;
            }
        });

        fsm.registerEvent(State.STOPPING, Evt.WAIT, new StateTransitionHandler<State, Evt>() {
            @Override
            public State run(final State fromState, final Evt withEvent, final Opt eventData) {
                followupEvent = Evt.MOVE;
                return State.WAITING;
            }
        });

        return fsm;
    }
}
