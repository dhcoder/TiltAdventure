package tiltadv.entity.components.data;

import com.badlogic.gdx.math.Vector2;
import dhcoder.support.math.Angle;
import dhcoder.support.opt.Opt;
import dhcoder.support.time.Duration;
import tiltadv.entity.AbstractComponent;
import tiltadv.entity.Entity;
import tiltadv.immutable.ImmutableVector2;

import static com.badlogic.gdx.math.MathUtils.sin;
import static dhcoder.support.utils.StringUtils.format;

/**
 * Component that encapsulates the logic of calculating an {@link Entity}'s velocity and acceleration. Expects the
 * existence of a {@link TransformComponent} to act upon.
 * <p/>
 * TODO: Add tests and documentation, maybe also remove some public methods here.
 * TODO: Do I really like the way we set velocity on this class? How about "setTargetVelocity" so we ramp up as well?
 */
public class MotionComponent extends AbstractComponent {

    /**
     * An optional value which, if set, clamps the maximum velocity for this entity.
     */
    public final Opt<Float> maxVelocityOpt = Opt.withNoValue();

    // The velocity of this entity is measured in in pixels/sec
    private final Vector2 startVelocity = new Vector2();
    private final Vector2 currentVelocity = new Vector2();
    private final ImmutableVector2 immutableCurrentVelocity = new ImmutableVector2(currentVelocity);
    private final Vector2 targetVelocity = new Vector2();

    // An optional duration, which, if set, represents the amount of time it takes for the current velocity to converge
    // into the target velocity. If 0, the target velocity is reached immediately.
    private Duration acclerationTime = Duration.zero();
    // Time passed since the velocity was last set.
    private Duration timeElapsedSoFar = Duration.zero();

    private TransformComponent transformComponent;

    public void setTargetVelocity(final Vector2 velocity) {
        setTargetVelocity(velocity.x, velocity.y);
    }

    public void setTargetVelocity(final float vx, final float vy) {
        startVelocity.set(currentVelocity);
        targetVelocity.set(vx, vy);
        if (maxVelocityOpt.hasValue()) {
            targetVelocity.limit(maxVelocityOpt.getValue());
        }
        timeElapsedSoFar.setZero();
    }

    public ImmutableVector2 getVelocity() {
        return immutableCurrentVelocity;
    }

    public void setAccelerationTime(final Duration accelerationTime) {
        this.acclerationTime.setFrom(accelerationTime);
    }

    @Override
    public void initialize(final Entity owner) {
        transformComponent = owner.requireComponent(TransformComponent.class);
    }

    @Override
    public void update(final Duration elapsedTime) {

        if (!currentVelocity.equals(targetVelocity)) {
            float secsElapsedSoFar = timeElapsedSoFar.getSeconds();
            float accelTimeInSecs = acclerationTime.getSeconds();
            if (secsElapsedSoFar < accelTimeInSecs) {
                // Sin(π) -> Sin(π/2) => 0 -> 1, but in a way with a smooth decelerating curve.
                // Map how much time has passed to the appropriate point on the sin curve.
                float timeToRadians = Angle.PI - Angle.HALF_PI * secsElapsedSoFar / accelTimeInSecs;
                float percentComplete = sin(timeToRadians);
                currentVelocity.set(startVelocity.x + (targetVelocity.x - startVelocity.x) * percentComplete,
                    startVelocity.y + (targetVelocity.y - startVelocity.y) * percentComplete);
                timeElapsedSoFar.add(elapsedTime);
            }
            else {
                currentVelocity.set(targetVelocity);
            }
        }

        // Adjust current position based on how much velocity was applied over the last time range
        transformComponent.translate.mulAdd(currentVelocity, elapsedTime.getSeconds());
    }
}
