package tiltadv.entity.components.data;

import com.badlogic.gdx.math.Vector2;
import dhcoder.support.math.Angle;
import dhcoder.support.opt.Opt;
import dhcoder.support.time.Duration;
import tiltadv.entity.AbstractComponent;
import tiltadv.entity.Entity;

import static com.badlogic.gdx.math.MathUtils.sin;

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

    // The velocity of this entity, in pixels/sec
    private final Vector2 velocity = new Vector2();
    // The acceleration of this entity, in pixels/sec²
    private final Vector2 acceleration = new Vector2();

    /**
     * An optional duration (in seconds), which, if set, represents the amount of time after which, if set, this motion
     * comes to a complete stop.
     */
    private final Opt<Float> dampingTimeOpt = Opt.withNoValue();
    private final Vector2 velocityCopy = new Vector2(); // Copy of pre-dampened velocity, used for damping calculations
    private float timeElapsedSoFar; // In seconds
    private TransformComponent transformComponent;

    public void setVelocity(final Vector2 velocity) {
        setVelocity(velocity.x, velocity.y);
    }

    public void setVelocity(final float vx, final float vy) {
        velocity.set(vx, vy);
        limitVelocity();
        dampingTimeOpt.clear();
    }

    public void setAcceleration(final Vector2 acceleration) {
        setAcceleration(acceleration.x, acceleration.y);
    }

    public void setAcceleration(final float ax, final float ay) {
        acceleration.set(ax, ay);
    }

    public void setDampingTime(final float dampingTime) {
        acceleration.set(0f, 0f);
        velocityCopy.set(velocity);
        dampingTimeOpt.set(dampingTime);
        timeElapsedSoFar = 0;
    }

    @Override
    public void initialize(final Entity owner) {
        transformComponent = owner.requireComponent(TransformComponent.class);
    }

    @Override
    public void update(final Duration elapsedTime) {

        float elapsedSecs = elapsedTime.inSeconds();

        if (dampingTimeOpt.hasValue()) {
            float dampingTime = dampingTimeOpt.value();
            if (timeElapsedSoFar < dampingTime) {
                // Sin(π/2) -> Sin(π) => 1 -> 0, but in a way with a smooth decelerating curve.
                // Map how much time has passed to the appropriate point on the sin curve.
                float timeToRadians = Angle.HALF_PI + Angle.HALF_PI * timeElapsedSoFar / dampingTime;
                velocity.set(velocityCopy);
                velocity.scl(sin(timeToRadians));
                timeElapsedSoFar += elapsedSecs;
            } else {
                velocity.setZero();
                dampingTimeOpt.clear();
            }
        } else if (!acceleration.isZero()) {
            velocity.mulAdd(acceleration, elapsedSecs);
            limitVelocity();
        }

        // Adjust current position based on how much velocity was applied over the last time range
        transformComponent.translate.mulAdd(velocity, elapsedSecs);
    }

    // Make sure the current velocity is bounded by the current maximum velocity. You must remember to call this any
    // time velocity is potentially increased.
    private void limitVelocity() {
        if (maxVelocityOpt.hasValue()) {
            velocity.limit(maxVelocityOpt.value());
        }
    }
}
