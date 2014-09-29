package tiltadv.components.collision;

import dhcoder.libgdx.collision.Collision;
import dhcoder.libgdx.collision.CollisionSystem;
import dhcoder.libgdx.entity.Entity;
import tiltadv.components.combat.HealthComponent;
import tiltadv.globals.Group;
import tiltadv.globals.Services;

/**
 * Component that maintains the collision logic for the main player's avatar.
 */
public final class PlayerSensorCollisionComponent extends CollisionComponent {

    static {
        CollisionSystem collisionSystem = Services.get(CollisionSystem.class);
        collisionSystem.registerCollidesWith(Group.PLAYER_SENSOR, Group.ENEMY);
    }

//    private SwordBehaviorComponent swordBehaviorComponent;

    public PlayerSensorCollisionComponent() {
        super(Group.PLAYER_SENSOR);
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

    private void handleEnemyCollision(final Collision collision) {
        Entity enemyEntity = (Entity)collision.getTarget().getTag().getValue();
        final HealthComponent healthComponent = enemyEntity.requireComponent(HealthComponent.class);
        if (healthComponent.canTakeDamage()) {
//            swordBehaviorComponent.swing();
        }
    }
}
