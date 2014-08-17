package tiltadv.components.behavior;

import com.badlogic.gdx.math.Vector2;
import dhcoder.libgdx.entity.AbstractComponent;
import dhcoder.libgdx.entity.Entity;
import dhcoder.support.time.Duration;
import tiltadv.components.model.MotionComponent;
import tiltadv.components.model.TiltComponent;
import tiltadv.memory.Pools;

/**
 * Component that maintains the state and logic of the main player's avatar.
 */
public final class PlayerBehaviorComponent extends AbstractComponent {

    private static final float TILT_MULTIPLIER = 70f;
    private static final Duration STOP_DURATION = Duration.fromSeconds(.3f);
    private TiltComponent tiltComponent;
    private MotionComponent motionComponent;
    private boolean isMoving;

    @Override
    public void initialize(final Entity owner) {
        tiltComponent = owner.requireComponent(TiltComponent.class);
        motionComponent = owner.requireComponent(MotionComponent.class);
    }

    @Override
    public void update(final Duration elapsedTime) {
        Vector2 tilt = tiltComponent.getTilt();

        if (!tilt.isZero()) {
            {
                Vector2 velocity = Pools.vector2s.grabNew();
                velocity.set(tilt.x * TILT_MULTIPLIER, tilt.y * TILT_MULTIPLIER);
                motionComponent.setVelocity(velocity);
                Pools.vector2s.free(velocity);
            }
            isMoving = true;
        }
        else {
            if (isMoving) {
                motionComponent.stopSmoothly(STOP_DURATION);
                isMoving = false;
            }
        }
    }
}
