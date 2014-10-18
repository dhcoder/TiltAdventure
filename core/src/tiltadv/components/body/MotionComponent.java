package tiltadv.components.body;

import com.badlogic.gdx.math.Vector2;
import dhcoder.libgdx.entity.AbstractComponent;
import dhcoder.libgdx.entity.Entity;
import dhcoder.support.time.Duration;
import tiltadv.memory.Pools;

/**
 * Component that encapsulates the logic of calculating an {@link Entity}'s velocity and acceleration. Expects the
 * existence of a {@link PositionComponent} to act upon.
 * <p/>
 * TODO: Add tests and documentation, maybe also remove some public methods here.
 */
public final class MotionComponent extends AbstractComponent {

    // The velocity of this entity is measured in pixels/sec
    private final Vector2 velocity = new Vector2();
    // The velocity of this entity is measured in pixels/sec² (after one sec, velocity should be reduced by this much)
    private final Vector2 deceleration = new Vector2();
    private final Duration lockDuration = Duration.zero();

    private PositionComponent positionComponent;

    public Vector2 getVelocity() { return velocity; }

    public MotionComponent setVelocity(final Vector2 velocity) {
        if (!lockDuration.isZero()) {
            return this;
        }

        this.velocity.set(velocity);
        deceleration.setZero();
        return this;
    }

    public MotionComponent adjustVelocity(final Vector2 adjustmentVelocity) {
        if (!lockDuration.isZero()) {
            return this;
        }

        this.velocity.add(adjustmentVelocity);
        return this;
    }

    // Set a velocity on this entity which starts decelerating immediately, emulating a sudden push.
    public MotionComponent setImpulse(final Vector2 impulse, final Duration time) {
        if (!lockDuration.isZero()) {
            return this;
        }

        velocity.set(impulse);
        setLocked(time);
        return this;
    }

    public MotionComponent setLocked(final Duration duration) {
        if (lockDuration.isZero()) {
            stopSmoothly(duration);
        }

        if (lockDuration.getSeconds() < duration.getSeconds()) {
            lockDuration.setFrom(duration); // Longer lock wins
        }

        return this;
    }

    /**
     * Begin decelerating the entity, such that it takes exactly the specified amount of time to stop.
     */
    public void stopSmoothly(final Duration time) {
        if (!lockDuration.isZero()) {
            return;
        }

        if (time.isZero()) {
            velocity.setZero();
            return;
        }

        float timeSecs = time.getSeconds();
        deceleration.set(-velocity.x / timeSecs, -velocity.y / timeSecs);
    }

    @Override
    public void initialize(final Entity owner) {
        positionComponent = owner.requireComponent(PositionComponent.class);
    }

    @Override
    public void update(final Duration elapsedTime) {
        if (!lockDuration.isZero()) {
            lockDuration.subtract(elapsedTime);
        }

        if (!deceleration.isZero()) {
            velocity.mulAdd(deceleration, elapsedTime.getSeconds());

            // If velocity and deceleration are going in the same direction on either axis, we've gone too far (and
            // this would probably be due to a precision error), so treat this condition as stopping.
            if (velocity.x * deceleration.x > 0 || velocity.y * deceleration.y > 0) {
                velocity.setZero();
                deceleration.setZero();
            }
        }

        // Adjust current position based on how much velocity was applied over the last time range
        {
            Vector2 translate = Pools.vector2s.grabNew();
            translate.set(positionComponent.getPosition());
            translate.mulAdd(velocity, elapsedTime.getSeconds());
            positionComponent.setPosition(translate);
            Pools.vector2s.free(translate);
        }
    }

    @Override
    public void reset() {
        velocity.setZero();
        deceleration.setZero();
        lockDuration.setZero();

        positionComponent = null;
    }
}
