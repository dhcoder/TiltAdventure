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
import tiltadv.components.dynamics.PositionComponent;
import tiltadv.components.dynamics.box2d.BodyComponent;
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

    private static final float SPEED = 5f;
    private static final float PROJECTILE_SPEED = SPEED * 30f;
    private static final float SHOOTING_CHANCE = 1f;
    private static final Duration STOPPING_DURATION = Duration.fromSeconds(0.3f);
    private static final Random random = new Random();
    private final Duration remainingDuration = Duration.zero();
    private final StateMachine<State, Evt> octoState;
    private Evt followupEvent;
    private Entity owner;
    private PositionComponent positionComponent;
    private BodyComponent bodyComponent;

    public OctoBehaviorComponent() {
        octoState = createStateMachine();
        reset();
    }

    @Override
    public void onHurt() {}

    @Override
    public void onDied() {
        owner.getManager().freeEntity(owner);
    }

    @Override
    public void initialize(final Entity owner) {
        this.owner = owner;

        positionComponent = owner.requireComponent(PositionComponent.class);
        bodyComponent = owner.requireComponent(BodyComponent.class);

        owner.requireComponent(HealthComponent.class).setListener(this);
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
        bodyComponent = null;
        positionComponent = null;
    }

    private StateMachine<State, Evt> createStateMachine() {
        StateMachine<State, Evt> fsm = new StateMachine<State, Evt>(State.WAITING);

        fsm.registerEvent(State.WAITING, Evt.MOVE, new StateTransitionHandler<State, Evt>() {
            @Override
            public State run(final State fromState, final Evt withEvent, final Opt eventData) {
                Vector2 velocity = Pools.vector2s.grabNew();
                CardinalDirection nextDirection = CardinalDirection.getRandom();
                bodyComponent.setHeading(nextDirection.getAngle());
                velocity.set(SPEED, 0f);
                velocity.rotate(nextDirection.getAngle().getDegrees());
                bodyComponent.setVelocity(velocity);
                Pools.vector2s.free(velocity);
                followupEvent = Evt.WAIT;
                return State.MOVING;
            }
        });

        fsm.registerEvent(State.WAITING, Evt.SHOOT, new StateTransitionHandler<State, Evt>() {
            @Override
            public State run(final State fromState, final Evt withEvent, final Opt eventData) {
                final Entity rock = owner.getManager().newEntityFromTemplate(EntityId.OCTO_ROCK);
                final BodyComponent rockBody = rock.requireComponent(BodyComponent.class);
                rockBody.setDesiredPosition(positionComponent.getPosition());

                CardinalDirection direction = CardinalDirection.getForAngle(bodyComponent.getHeading());
                Vector2 rockVelocity = Pools.vector2s.grabNew();
                rockVelocity.set(PROJECTILE_SPEED, 0f);
                rockVelocity.rotate(direction.getAngle().getDegrees());
                rockBody.setVelocity(rockVelocity);
                Pools.vector2s.free(rockVelocity);

                followupEvent = Evt.MOVE;
                return State.WAITING;
            }
        });

        fsm.registerEvent(State.MOVING, Evt.WAIT, new StateTransitionHandler<State, Evt>() {
            @Override
            public State run(final State fromState, final Evt withEvent, final Opt eventData) {
                bodyComponent.stopSmoothly();
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
