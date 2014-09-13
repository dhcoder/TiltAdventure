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
public final class EnemyProjectileCollisionComponent extends CollisionComponent {

    static {
        CollisionSystem collisionSystem = Services.get(CollisionSystem.class);
        collisionSystem.registerCollidesWith(Group.ENEMY_PROJECTILE, Group.OBSTACLES | Group.PLAYER);
    }

    public EnemyProjectileCollisionComponent() {
        super(Group.ENEMY_PROJECTILE);
    }

    @Override
    protected void handleCollided(final Collision collision) {

        if (collision.getTarget().getGroupId() == Group.PLAYER) {
            Entity playerEntity = (Entity)collision.getTarget().getTag().getValue();
            PlayerBehaviorComponent playerBehavior = playerEntity.requireComponent(PlayerBehaviorComponent.class);

            Vector2 collisionDirection = Pools.vector2s.grabNew();
            collision.getRepulsionBetweenColliders(collisionDirection);
            collisionDirection.nor().scl(-1f); // Flip this to the point of view of the player.
            playerBehavior.takeDamage(collisionDirection);
            Pools.vector2s.free(collisionDirection);
        }

        // Projectile dies on collision
        Entity sourceEntity = (Entity)collision.getSource().getTag().getValue();
        sourceEntity.getManager().freeEntity(sourceEntity);
    }
}
