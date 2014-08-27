package tiltadv.components.behavior;

import com.badlogic.gdx.math.Vector2;
import dhcoder.libgdx.entity.AbstractComponent;
import dhcoder.libgdx.entity.Entity;
import dhcoder.support.opt.Opt;
import dhcoder.support.state.StateMachine;
import dhcoder.support.state.StateTransitionHandler;
import dhcoder.support.time.Duration;
import tiltadv.components.display.SpriteComponent;
import tiltadv.components.model.MotionComponent;
import tiltadv.components.model.TiltComponent;
import tiltadv.memory.Pools;

/**
 * Component that maintains the state and logic of the main player's avatar.
 */
public final class PlayerBehaviorComponent extends AbstractComponent {

    private enum State {
        STOPPED,
        MOVING,
        PARALYZED,
    }

    private enum Evt {
        TAKE_DAMAGE,
        UPDATE,
    }

    private static final float TILT_MULTIPLIER = 70f;
    private static final float KNOCKBACK_MULTIPLIER = 150f;
    private static final Duration STOP_DURATION = Duration.fromSeconds(.3f);
    private static final Duration PARALYZED_DURATION = Duration.fromSeconds(2f);

    private final StateMachine<State, Evt> playerState;

    private Duration paralyzedDuration = Duration.zero();

    private TiltComponent tiltComponent;
    private MotionComponent motionComponent;
    private SpriteComponent spriteComponent;

    public PlayerBehaviorComponent() {
        playerState = createStateMachine();
    }

    @Override
    public void initialize(final Entity owner) {
        tiltComponent = owner.requireComponent(TiltComponent.class);
        motionComponent = owner.requireComponent(MotionComponent.class);
        spriteComponent = owner.requireComponent(SpriteComponent.class);
    }

    @Override
    public void update(final Duration elapsedTime) {
        playerState.handleEvent(Evt.UPDATE, elapsedTime);
    }

    public void takeDamage(final Vector2 damageVector) {
        playerState.handleEvent(Evt.TAKE_DAMAGE, damageVector);
    }

    @Override
    protected void resetComponent() {
        playerState.reset();
        paralyzedDuration.setZero();

        motionComponent = null;
        tiltComponent = null;
    }

    private StateMachine<State, Evt> createStateMachine() {
        StateMachine<State, Evt> fsm = new StateMachine<State, Evt>(State.STOPPED);

        fsm.registerEvent(State.STOPPED, Evt.UPDATE, new StateTransitionHandler<State, Evt>() {
            @Override
            public State run(final State fromState, final Evt withEvent, final Opt eventData) {
                Vector2 tilt = tiltComponent.getTilt();
                return tilt.isZero() ? State.STOPPED : State.MOVING;
            }
        });

        fsm.registerEvent(State.MOVING, Evt.UPDATE, new StateTransitionHandler<State, Evt>() {
            @Override
            public State run(final State fromState, final Evt withEvent, final Opt eventData) {
                Vector2 tilt = tiltComponent.getTilt();
                if (!tilt.isZero()) {
                    Vector2 velocity = Pools.vector2s.grabNew().set(tilt).scl(TILT_MULTIPLIER);
                    motionComponent.setVelocity(velocity);
                    Pools.vector2s.free(velocity);
                    return State.MOVING;
                }
                else {
                    motionComponent.stopSmoothly(STOP_DURATION);
                    return State.STOPPED;
                }
            }
        });

        fsm.registerEvent(State.MOVING, Evt.TAKE_DAMAGE, new StateTransitionHandler<State, Evt>() {
            @Override
            public State run(final State fromState, final Evt withEvent, final Opt eventData) {
                Vector2 damageVector = (Vector2)eventData.getValue();
                knockback(damageVector);
                return State.PARALYZED;
            }
        });

        fsm.registerEvent(State.STOPPED, Evt.TAKE_DAMAGE, new StateTransitionHandler<State, Evt>() {
            @Override
            public State run(final State fromState, final Evt withEvent, final Opt eventData) {
                Vector2 damageVector = (Vector2)eventData.getValue();
                knockback(damageVector);
                return State.PARALYZED;
            }
        });

        fsm.registerEvent(State.PARALYZED, Evt.UPDATE, new StateTransitionHandler<State, Evt>() {
            @Override
            public State run(final State fromState, final Evt withEvent, final Opt eventData) {
                Duration elapsedTime = (Duration)eventData.getValue();
                paralyzedDuration.add(elapsedTime);
                if (paralyzedDuration.getSeconds() >= PARALYZED_DURATION.getSeconds()) {
                    paralyzedDuration.setZero();
                    spriteComponent.setAlpha(1f);
                    return State.STOPPED;
                }
                return State.PARALYZED;
            }
        });

        return fsm;
    }

    private void knockback(final Vector2 direction) {
        Vector2 impulse = Pools.vector2s.grabNew().set(direction).scl(KNOCKBACK_MULTIPLIER);
        motionComponent.setImpulse(impulse, STOP_DURATION);
        Pools.vector2s.free(impulse);

        spriteComponent.setAlpha(0.5f);
    }
}
