package tiltadv.components.collision;

import com.badlogic.gdx.math.Vector2;
import dhcoder.libgdx.collision.Collision;
import dhcoder.libgdx.collision.CollisionSystem;
import dhcoder.libgdx.entity.Entity;
import dhcoder.support.time.Duration;
import tiltadv.components.body.MotionComponent;
import tiltadv.components.body.PositionComponent;
import tiltadv.globals.Group;
import tiltadv.globals.Services;
import tiltadv.memory.Pools;

/**
 * Component that maintains the collision logic for the main player's avatar.
 */
public final class GravityWellCollisionComponent extends CollisionComponent {

    static {
        CollisionSystem collisionSystem = Services.get(CollisionSystem.class);
        collisionSystem.registerCollidesWith(Group.GRAVITY_WELL, Group.ENEMY | Group.PLAYER);
    }

    private PositionComponent positionComponent;
    private final Duration lastFrameDuration = Duration.zero();

    @Override
    protected void handleUpdate(final Duration elapsedTime) {
        lastFrameDuration.setFrom(elapsedTime);
    }

    public GravityWellCollisionComponent() {
        super(Group.GRAVITY_WELL);
    }

    @Override
    protected void handleInitialize(final Entity owner) {
        positionComponent = owner.requireComponent(PositionComponent.class);
    }

    @Override
    protected void handleOverlapping(final Collision collision) {
        applyGravityTo((Entity)collision.getTarget().getTag().getValue());
    }

    @Override
    protected void handleReset() {
        positionComponent = null;
        lastFrameDuration.setZero();
    }

    private void applyGravityTo(final Entity entity) {
        PositionComponent targetPositionComponent = entity.requireComponent(PositionComponent.class);
        MotionComponent targetMotionComponent = entity.requireComponent(MotionComponent.class);

        Vector2 gravity =
            Pools.vector2s.grabNew().set(positionComponent.getPosition()).sub(targetPositionComponent.getPosition());

        gravity.scl(0.1f);
//        targetMotionComponent.adjustVelocity(gravity);

        gravity.add(targetPositionComponent.getPosition());
        targetPositionComponent.setPosition(gravity);

        Pools.vector2s.freeCount(1);
    }
}
