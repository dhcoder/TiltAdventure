package tiltadv.components.collision;

import dhcoder.libgdx.collision.Collision;
import dhcoder.libgdx.collision.CollisionSystem;
import dhcoder.libgdx.entity.Entity;
import dhcoder.libgdx.entity.EntityManager;
import tiltadv.globals.Group;
import tiltadv.globals.Services;

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
        // Projectile dies on collision
        EntityManager entities = Services.get(EntityManager.class);
        Entity sourceEntity = (Entity)collision.getSource().getTag().getValue();
        entities.freeEntity(sourceEntity);
    }
}
