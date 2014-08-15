package tiltadv.components.behavior;

import com.badlogic.gdx.math.Vector2;
import dhcoder.libgdx.entity.AbstractComponent;
import dhcoder.libgdx.entity.Entity;
import dhcoder.support.opt.Opt;
import dhcoder.support.state.StateMachine;
import dhcoder.support.state.StateTransitionHandler;
import dhcoder.support.time.Duration;
import tiltadv.components.model.MotionComponent;
import tiltadv.memory.Pools;

import java.util.Random;

/**
 * Component that maintains the state and logic of the octo enemy.
 */
public final class OctoBehaviorComponent extends AbstractComponent {

    private enum S {
        WAITING,
        MOVING,
        STOPPING,
    }

    private enum E {
        MOVE,
        WAIT,
    }

    private enum Direction {
        NORTH,
        SOUTH,
        EAST,
        WEST
    }

    private static final float SPEED = 30.0f;
    private static final Duration STOPPING_DURATION = Duration.fromSeconds(0.3f);
    private static final Random random = new Random();
    private final Duration remainingDuration = Duration.zero();
    private StateMachine<S, E> octoState;
    private MotionComponent motionComponent;
    private E followupEvent = E.MOVE;

    @Override
    public void initialize(final Entity owner) {
        motionComponent = owner.requireComponent(MotionComponent.class);
        octoState = createStateMachine();
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

    private StateMachine<S, E> createStateMachine() {
        StateMachine<S, E> fsm = new StateMachine<S, E>(S.WAITING);

        fsm.registerEvent(S.WAITING, E.MOVE, new StateTransitionHandler<S, E>() {
            @Override
            public S run(final S fromState, final E withEvent, final Opt eventData) {

                Vector2 velocity = Pools.vector2s.grabNew();
                Direction randomDir = Direction.values()[random.nextInt(4)];
                switch (randomDir) {
                    case NORTH:
                        velocity.set(0f, SPEED);
                        break;
                    case SOUTH:
                        velocity.set(0f, -SPEED);
                        break;
                    case EAST:
                        velocity.set(SPEED, 0f);
                        break;
                    case WEST:
                        velocity.set(-SPEED, 0f);
                        break;
                }
                motionComponent.setVelocity(velocity);
                Pools.vector2s.free(velocity);
                followupEvent = E.WAIT;
                return S.MOVING;
            }
        });

        fsm.registerEvent(S.MOVING, E.WAIT, new StateTransitionHandler<S, E>() {
            @Override
            public S run(final S fromState, final E withEvent, final Opt eventData) {
                motionComponent.stopSmoothly(STOPPING_DURATION);
                remainingDuration.setFrom(STOPPING_DURATION);
                return S.STOPPING;
            }
        });

        fsm.registerEvent(S.STOPPING, E.WAIT, new StateTransitionHandler<S, E>() {
            @Override
            public S run(final S fromState, final E withEvent, final Opt eventData) {
                followupEvent = E.MOVE;
                return S.WAITING;
            }
        });

        return fsm;
    }
}
