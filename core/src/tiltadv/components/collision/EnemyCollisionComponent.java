package tiltadv.components.collision;

import com.badlogic.gdx.math.Vector2;
import dhcoder.libgdx.collision.Collision;
import dhcoder.libgdx.collision.CollisionSystem;
import dhcoder.libgdx.entity.Entity;
import tiltadv.components.behavior.PlayerBehaviorComponent;
import tiltadv.globals.Group;
import tiltadv.globals.Services;
import tiltadv.memory.Pools;

/**
 * Component that maintains the collision logic for the main player's avatar.
 */
public final class EnemyCollisionComponent extends CollisionComponent {

    static {
        CollisionSystem collisionSystem = Services.get(CollisionSystem.class);
        collisionSystem.registerCollidesWith(Group.ENEMY, Group.OBSTACLES | Group.PLAYER);
    }

    public EnemyCollisionComponent() {
        super(Group.ENEMY);
    }

    @Override
    protected void handleCollided(final Collision collision) {
        final int groupId = collision.getTarget().getGroupId();
        if (groupId == Group.OBSTACLES) {
            CollisionSystem collisionSystem = Services.get(CollisionSystem.class);
            collisionSystem.extractSourceCollider(collision);
        }
        else if (groupId == Group.PLAYER) {
            handlePlayerCollision(collision);
        }
    }

    @Override
    protected void handleOverlapping(final Collision collision) {
        final int groupId = collision.getTarget().getGroupId();
        if (groupId == Group.PLAYER) {
            handlePlayerCollision(collision);
        }
    }

    private void handlePlayerCollision(final Collision collision) {
        Entity playerEntity = (Entity)collision.getTarget().getTag().getValue();
        PlayerBehaviorComponent playerBehavior = playerEntity.requireComponent(PlayerBehaviorComponent.class);
        if (!playerBehavior.canTakeDamage()) {
            return;
        }

        Vector2 collisionDirection = Pools.vector2s.grabNew();
        collision.getRepulsionBetweenColliders(collisionDirection);
        collisionDirection.nor().scl(-1f); // Flip this to the point of view of the player.
        playerBehavior.takeDamage(collisionDirection);
        Pools.vector2s.free(collisionDirection);
    }
}
