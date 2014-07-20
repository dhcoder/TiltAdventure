package tiltadv.entity.components.behavior;

import com.badlogic.gdx.math.Vector2;
import dhcoder.support.time.Duration;
import tiltadv.entity.AbstractComponent;
import tiltadv.entity.Entity;
import tiltadv.entity.components.data.MotionComponent;
import tiltadv.entity.components.data.TiltComponent;

/**
 * Component that maintains the state and logic of the main player's avatar.
 */
public class PlayerBehaviorComponent extends AbstractComponent {

    private static final float TILT_MULTIPLIER = 50f;
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
            motionComponent.setVelocity(tilt.x * TILT_MULTIPLIER, tilt.y * TILT_MULTIPLIER);
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
