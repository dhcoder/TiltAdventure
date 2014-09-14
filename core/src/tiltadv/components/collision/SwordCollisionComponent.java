package tiltadv.components.collision;

import com.badlogic.gdx.math.Vector2;
import dhcoder.libgdx.collision.Collision;
import dhcoder.libgdx.collision.CollisionSystem;
import dhcoder.libgdx.entity.Entity;
import tiltadv.components.combat.AttackComponent;
import tiltadv.components.combat.HealthComponent;
import tiltadv.globals.Group;
import tiltadv.globals.Services;
import tiltadv.memory.Pools;

/**
 * Component that maintains the collision logic for the main player's avatar.
 */
public final class SwordCollisionComponent extends CollisionComponent {

    static {
        CollisionSystem collisionSystem = Services.get(CollisionSystem.class);
        collisionSystem.registerCollidesWith(Group.PLAYER_SWORD, Group.ENEMY);
    }

    private AttackComponent attackComponent;

    public SwordCollisionComponent() {
        super(Group.PLAYER_SWORD);
    }

    @Override
    protected void handleInitialize(final Entity owner) {
        attackComponent = owner.requireComponent(AttackComponent.class);
    }

    @Override
    protected void handleCollided(final Collision collision) {
        if (collision.getTarget().getGroupId() == Group.ENEMY) {
            handleEnemyCollision(collision);
        }
    }

    @Override
    protected void handleOverlapping(final Collision collision) {
        if (collision.getTarget().getGroupId() == Group.ENEMY) {
            handleEnemyCollision(collision);
        }
    }

    @Override
    protected void handleReset() {
        attackComponent = null;
    }

    private void handleEnemyCollision(final Collision collision) {
        Entity enemyEntity = (Entity)collision.getTarget().getTag().getValue();
        HealthComponent healthComponent = enemyEntity.requireComponent(HealthComponent.class);
        if (!healthComponent.canTakeDamage()) { return; }

        Vector2 collisionDirection = Pools.vector2s.grabNew();
        collision.getRepulsionBetweenColliders(collisionDirection);
        collisionDirection.nor().scl(-1f); // Flip this to the point of view of the player.
        healthComponent.takeDamage(collisionDirection, attackComponent.getStrength());
        Pools.vector2s.free(collisionDirection);
    }
}
