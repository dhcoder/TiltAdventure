package tiltadv.components.collision;

import dhcoder.libgdx.collision.Collision;
import dhcoder.libgdx.collision.CollisionSystem;
import dhcoder.libgdx.collision.shape.Shape;
import tiltadv.globals.Services;
import tiltadv.globals.Group;

/**
 * Component that maintains the collision logic for the main player's avatar.
 */
public final class PlayerCollisionComponent extends CollisionComponent {

    static {
        CollisionSystem collisionSystem = Services.get(CollisionSystem.class);
        collisionSystem.registerCollidesWith(Group.PLAYER, Group.OBSTACLES);
    }

    public PlayerCollisionComponent(final Shape shape) {
        super(Group.PLAYER, shape);
    }

    @Override
    protected void handleCollided(final Collision collision) {
        if (collision.getTarget().getGroupId() == Group.OBSTACLES) {
            CollisionSystem collisionSystem = Services.get(CollisionSystem.class);
            collisionSystem.revertCollision(collision);
            syncEntityWithCollisionSystem();
        }
    }
}
