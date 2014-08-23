package tiltadv.components.collision;

import dhcoder.libgdx.collision.Collision;
import dhcoder.libgdx.collision.CollisionSystem;
import tiltadv.globals.Group;
import tiltadv.globals.Services;

/**
 * Component that maintains the collision logic for the main player's avatar.
 */
public final class EnemyCollisionComponent extends CollisionComponent {

    static {
        CollisionSystem collisionSystem = Services.get(CollisionSystem.class);
        collisionSystem.registerCollidesWith(Group.ENEMY, Group.OBSTACLES);
    }

    public EnemyCollisionComponent() {
        super(Group.ENEMY);
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
