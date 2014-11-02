package tiltadv.components.behavior;

import com.badlogic.gdx.math.Vector2;
import dhcoder.libgdx.entity.AbstractComponent;
import dhcoder.libgdx.entity.Entity;
import dhcoder.support.event.EventListener;
import dhcoder.support.opt.Opt;
import dhcoder.support.state.StateMachine;
import dhcoder.support.state.StateTransitionHandler;
import dhcoder.support.time.Duration;
import tiltadv.components.dynamics.PositionComponent;
import tiltadv.components.dynamics.TiltComponent;
import tiltadv.components.dynamics.box2d.BodyComponent;
import tiltadv.components.combat.HealthComponent;
import tiltadv.globals.Events;
import tiltadv.globals.Services;
import tiltadv.input.Vibrator;
import tiltadv.memory.Pools;

/**
 * Component that maintains the state and logic of the main player's avatar.
 */
public final class PlayerBehaviorComponent extends AbstractComponent implements HealthComponent.Listener {

    private enum State {
        STANDING,
        MOVING,
        PARALYZED,
    }

    private enum Evt {
        PARALYZE,
        UPDATE,
    }

    private static final float TILT_MULTIPLIER = 6f;
    private static final Duration FROZEN_DURATION = Duration.fromSeconds(.3f);

    private final StateMachine<State, Evt> playerState;
    private final Duration frozenDuration = Duration.zero();
    private final Opt<Entity> selectedTargetOpt = Opt.withNoValue();
    private final EventListener handleTargetSelected = new EventListener() {
        @Override
        public void run(final Object sender) {
            selectedTargetOpt.set((Entity)sender);
        }
    };

    private final EventListener handleTargetCleared = new EventListener() {
        @Override
        public void run(final Object sender) {
            selectedTargetOpt.clear();
        }
    };

    private Entity owner;
    private Entity swordEntity;
    private PositionComponent positionComponent;
    private BodyComponent bodyComponent;
    private TiltComponent tiltComponent;

    public PlayerBehaviorComponent() {
        playerState = createStateMachine();
    }

    @Override
    public void initialize(final Entity owner) {
        this.owner = owner;
        positionComponent = owner.requireComponent(PositionComponent.class);
        bodyComponent = owner.requireComponent(BodyComponent.class);
        tiltComponent = owner.requireComponent(TiltComponent.class);
        owner.requireComponent(HealthComponent.class).setListener(this);

        Events.onTargetSelected.addListener(handleTargetSelected);
        Events.onTargetCleared.addListener(handleTargetCleared);
    }

    @Override
    public void update(final Duration elapsedTime) {
        playerState.handleEvent(Evt.UPDATE, elapsedTime);

        if (selectedTargetOpt.hasValue()) {
            final Entity target = selectedTargetOpt.getValue();
            final Vector2 targetPos = target.requireComponent(PositionComponent.class).getPosition();
            final Vector2 vectorToTarget = Pools.vector2s.grabNew();
            vectorToTarget.set(targetPos).sub(positionComponent.getPosition());
            bodyComponent.setHeadingFrom(vectorToTarget);
            Pools.vector2s.freeCount(1);
        }
        else if (playerState.getCurrentState() == State.MOVING) {
            bodyComponent.setHeadingFrom(tiltComponent.getTilt());
        }
    }

    @Override
    public void reset() {
        owner.getManager().freeEntity(swordEntity);
        Events.onTargetSelected.removeListener(handleTargetSelected);
        Events.onTargetCleared.removeListener(handleTargetSelected);

        swordEntity = null;
        owner = null;

        playerState.reset();
        frozenDuration.setZero();

        positionComponent = null;
        tiltComponent = null;

        selectedTargetOpt.clear();
    }

    @Override
    public void onHurt() {
        playerState.handleEvent(Evt.PARALYZE);
        Vibrator vibrator = Services.get(Vibrator.class);
        vibrator.vibrate(Vibrator.SHORT);
    }

    @Override
    public void onDied() {
        // TODO: Handle death later...
        onHurt();
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
                    bodyComponent.setVelocity(velocity);
                    Pools.vector2s.free(velocity);
                    return State.MOVING;
                }
                else {
                    return State.STANDING;
                }
            }
        });

        fsm.registerEvent(State.MOVING, Evt.PARALYZE, new StateTransitionHandler<State, Evt>() {
            @Override
            public State run(final State fromState, final Evt withEvent, final Opt eventData) {
                return State.PARALYZED;
            }
        });

        fsm.registerEvent(State.STANDING, Evt.PARALYZE, new StateTransitionHandler<State, Evt>() {
            @Override
            public State run(final State fromState, final Evt withEvent, final Opt eventData) {
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
}
