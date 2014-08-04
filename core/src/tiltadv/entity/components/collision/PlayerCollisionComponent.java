package tiltadv.entity.components.collision;

import dhcoder.support.collision.Collision;
import dhcoder.support.collision.CollisionSystem;
import dhcoder.support.collision.shape.Shape;
import tiltadv.Services;
import tiltadv.constants.Group;

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
            collisionSystem.separateSourceFromCollision(collision);
            syncEntityWithCollisionSystem();
        }
    }
}
