package tiltadv.components.collision;

import dhcoder.libgdx.collision.Collision;
import dhcoder.libgdx.collision.CollisionSystem;
import dhcoder.libgdx.collision.shape.Shape;
import tiltadv.globals.Services;
import tiltadv.globals.Group;

/**
 * Component that maintains the collision logic for the main player's avatar.
 */
public final class EnemyCollisionComponent extends CollisionComponent {

    static {
        CollisionSystem collisionSystem = Services.get(CollisionSystem.class);
        collisionSystem.registerCollidesWith(Group.ENEMY, Group.OBSTACLES);
    }

    public EnemyCollisionComponent(final Shape shape) {
        super(Group.ENEMY, shape);
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
