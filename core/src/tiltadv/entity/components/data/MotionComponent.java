package tiltadv.entity.components.data;

import com.badlogic.gdx.math.Vector2;
import dhcoder.support.immutable.ImmutableDuration;
import tiltadv.entity.AbstractComponent;
import tiltadv.entity.Entity;
import tiltadv.immutable.ImmutableVector2;

/**
 * Component that encapsulates the logic of calculating an {@link Entity}'s velocity and acceleration. Expects the
 * existence of a {@link TransformComponent} to act upon.
 * <p/>
 * TODO: Add tests and documentation, maybe also remove some public methods here.
 * TODO: Do I really like the way we set velocity on this class? How about "setTargetVelocity" so we ramp up as well?
 */
public class MotionComponent extends AbstractComponent {

    // The velocity of this entity is measured in pixels/sec
    private final Vector2 velocity = new Vector2();
    private final ImmutableVector2 immutableVelocity = new ImmutableVector2(velocity);
    // The velocity of this entity is measured in pixels/sec² (after one sec, velocity should be reduced by this much)
    private final Vector2 deceleration = new Vector2();
    private TransformComponent transformComponent;

    public void setVelocity(final float vx, final float vy) {
        velocity.set(vx, vy);
        deceleration.setZero();
    }

    public void setVelocity(final ImmutableVector2 vector) {
        setVelocity(vector.getX(), vector.getY());
    }

    public ImmutableVector2 getVelocity() { return immutableVelocity; }

    public void smoothStop(final ImmutableDuration time) {
        float timeSecs = time.getSeconds();
        deceleration.set(-velocity.x / timeSecs, -velocity.y / timeSecs);
    }

    @Override
    public void initialize(final Entity owner) {
        transformComponent = owner.requireComponent(TransformComponent.class);
    }

    @Override
    public void update(final ImmutableDuration elapsedTime) {

        if (!deceleration.isZero()) {
            velocity.mulAdd(deceleration, elapsedTime.getSeconds());

            if (velocity.x * deceleration.x > 0 || velocity.y * deceleration.y > 0) {
                velocity.setZero();
                deceleration.setZero();
            }
        }

        // Adjust current position based on how much velocity was applied over the last time range
        transformComponent.translate.mulAdd(velocity, elapsedTime.getSeconds());
    }
}
