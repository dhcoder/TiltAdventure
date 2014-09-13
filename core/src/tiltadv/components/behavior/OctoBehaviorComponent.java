package tiltadv.components.behavior;

import com.badlogic.gdx.math.Vector2;
import dhcoder.libgdx.entity.AbstractComponent;
import dhcoder.libgdx.entity.Entity;
import dhcoder.support.math.CardinalDirection;
import dhcoder.support.opt.Opt;
import dhcoder.support.state.StateMachine;
import dhcoder.support.state.StateTransitionHandler;
import dhcoder.support.time.Duration;
import tiltadv.components.combat.HealthComponent;
import tiltadv.components.model.HeadingComponent;
import tiltadv.components.model.MotionComponent;
import tiltadv.components.model.TransformComponent;
import tiltadv.globals.EntityId;
import tiltadv.memory.Pools;

import java.util.Random;

/**
 * Component that maintains the state and logic of the octo enemy.
 */
public final class OctoBehaviorComponent extends AbstractComponent implements HealthComponent.Listener {
    private enum State {
        WAITING,
        MOVING,
        STOPPING,
    }

    private enum Evt {
        MOVE,
        WAIT,
        SHOOT,
    }

    private static final float SPEED = 30.0f;
    private static final float PROJECTILE_SPEED = SPEED * 5f;
    private static final float SHOOTING_CHANCE = 1f;
    private static final Duration STOPPING_DURATION = Duration.fromSeconds(0.3f);
    private static final Random random = new Random();
    private final Duration remainingDuration = Duration.zero();
    private final StateMachine<State, Evt> octoState;
    private Evt followupEvent;
    private Entity owner;
    private HeadingComponent headingComponent;
    private MotionComponent motionComponent;
    private TransformComponent transformComponent;

    public OctoBehaviorComponent() {
        octoState = createStateMachine();
        reset();
    }

    @Override
    public void onHurt() {

    }

    @Override
    public void onDied() {
        owner.getManager().freeEntity(owner);
    }

    @Override
    public void initialize(final Entity owner) {
        this.owner = owner;

        headingComponent = owner.requireComponent(HeadingComponent.class);
        motionComponent = owner.requireComponent(MotionComponent.class);
        transformComponent = owner.requireComponent(TransformComponent.class);
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
    public void reset() {
        octoState.reset();
        followupEvent = Evt.MOVE;
        remainingDuration.setZero();

        owner = null;
        headingComponent = null;
        motionComponent = null;
        transformComponent = null;
    }

    private StateMachine<State, Evt> createStateMachine() {
        StateMachine<State, Evt> fsm = new StateMachine<State, Evt>(State.WAITING);

        fsm.registerEvent(State.WAITING, Evt.MOVE, new StateTransitionHandler<State, Evt>() {
            @Override
            public State run(final State fromState, final Evt withEvent, final Opt eventData) {

                Vector2 velocity = Pools.vector2s.grabNew();
                CardinalDirection nextDirection = CardinalDirection.getRandom();
                headingComponent.setHeading(nextDirection.getAngle());
                velocity.set(SPEED, 0f);
                velocity.rotate(nextDirection.getAngle().getDegrees());
                motionComponent.setVelocity(velocity);
                Pools.vector2s.free(velocity);
                followupEvent = Evt.WAIT;
                return State.MOVING;
            }
        });

        fsm.registerEvent(State.WAITING, Evt.SHOOT, new StateTransitionHandler<State, Evt>() {
            @Override
            public State run(final State fromState, final Evt withEvent, final Opt eventData) {
                final Entity rock = owner.getManager().newEntityFromTemplate(EntityId.OCTO_ROCK);
                final MotionComponent rockMotion = rock.requireComponent(MotionComponent.class);
                final TransformComponent rockPosition = rock.requireComponent(TransformComponent.class);
                rockPosition.setTranslate(transformComponent.getTranslate());

                CardinalDirection direction = CardinalDirection.getForAngle(headingComponent.getHeading());
                Vector2 rockVelocity = Pools.vector2s.grabNew();
                rockVelocity.set(PROJECTILE_SPEED, 0f);
                rockVelocity.rotate(direction.getAngle().getDegrees());
                rockMotion.setVelocity(rockVelocity);
                Pools.vector2s.free(rockVelocity);

                followupEvent = Evt.MOVE;
                return State.WAITING;
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
                followupEvent = random.nextFloat() <= SHOOTING_CHANCE ? Evt.SHOOT : Evt.MOVE;
                return State.WAITING;
            }
        });

        return fsm;
    }
}
