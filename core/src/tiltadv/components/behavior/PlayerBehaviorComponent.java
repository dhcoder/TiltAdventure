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
        STANDING,
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
    private static final Duration FROZEN_DURATION = Duration.fromSeconds(.3f);
    private static final Duration INVINCIBLE_DURATION = Duration.fromSeconds(3f);

    private final StateMachine<State, Evt> playerState;
    private final Duration frozenDuration = Duration.zero();
    private final Duration invincibleDuration = Duration.zero();

    private boolean isInvincible;

    private TiltComponent tiltComponent;
    private MotionComponent motionComponent;
    private SpriteComponent spriteComponent;

    public PlayerBehaviorComponent() {
        playerState = createStateMachine();
    }

    private void setInvincible(final boolean isInvincible) {
        spriteComponent.setAlpha(isInvincible ? .5f : 1f);
        this.isInvincible = isInvincible;
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

        if (isInvincible) {
            invincibleDuration.add(elapsedTime);
            if (invincibleDuration.getSeconds() >= INVINCIBLE_DURATION.getSeconds()) {
                invincibleDuration.setZero();
                setInvincible(false);
            }
        }
    }

    public boolean takeDamage(final Vector2 damageVector) {
        if (isInvincible) {
            return false;
        }
        playerState.handleEvent(Evt.TAKE_DAMAGE, damageVector);
        return true;
    }

    @Override
    protected void resetComponent() {
        playerState.reset();
        frozenDuration.setZero();
        invincibleDuration.setZero();
        isInvincible = false;

        motionComponent = null;
        tiltComponent = null;
    }

    private StateMachine<State, Evt> createStateMachine() {
        StateMachine<State, Evt> fsm = new StateMachine<State, Evt>(State.STANDING);

        fsm.registerEvent(State.STANDING, Evt.UPDATE, new StateTransitionHandler<State, Evt>() {
            @Override
            public State run(final State fromState, final Evt withEvent, final Opt eventData) {
                Vector2 tilt = tiltComponent.getTilt();
                return tilt.isZero() ? State.STANDING : State.MOVING;
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
                    return State.STANDING;
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

        fsm.registerEvent(State.STANDING, Evt.TAKE_DAMAGE, new StateTransitionHandler<State, Evt>() {
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
                frozenDuration.add(elapsedTime);
                if (frozenDuration.getSeconds() >= FROZEN_DURATION.getSeconds()) {
                    frozenDuration.setZero();
                    return State.STANDING;
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

        setInvincible(true);
    }
}
