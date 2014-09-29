package tiltadv.components.collision;

import dhcoder.libgdx.collision.Collision;
import dhcoder.libgdx.collision.CollisionSystem;
import tiltadv.globals.Group;
import tiltadv.globals.Services;

/**
 * Component that maintains the collision logic for the main player's avatar.
 */
public final class TestCollisionComponent extends CollisionComponent {

    static {
        CollisionSystem collisionSystem = Services.get(CollisionSystem.class);
        collisionSystem.registerCollidesWith(Group.PLAYER_TEST, Group.OBSTACLES);
    }

    public TestCollisionComponent() {
        super(Group.PLAYER_TEST);
    }

    @Override
    protected void handleCollided(final Collision collision) {
        if (collision.getTarget().getGroupId() == Group.OBSTACLES) {
            CollisionSystem collisionSystem = Services.get(CollisionSystem.class);
            collisionSystem.extractSourceCollider(collision);
        }
    }
}
