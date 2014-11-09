package tiltadv.collision;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import dhcoder.libgdx.entity.Entity;
import dhcoder.libgdx.physics.AbstractCollisionHandler;
import tiltadv.components.combat.AttackComponent;
import tiltadv.components.combat.HealthComponent;
import tiltadv.components.combat.KnockbackComponent;
import tiltadv.components.dynamics.PositionComponent;
import tiltadv.components.hierarchy.ParentComponent;
import tiltadv.memory.Pools;

/**
 * Handler for when an enemy touches a player.
 */
public final class SwordEnemyCollisionHandler extends AbstractCollisionHandler {
    @Override
    public void onCollided(final Body bodyA, final Body bodyB) {
        Entity enemyEntity = (Entity)bodyB.getUserData();
        HealthComponent healthComponent = enemyEntity.requireComponent(HealthComponent.class);
        if (!healthComponent.canTakeDamage()) {
            return;
        }

        Entity swordEntity = (Entity)bodyA.getUserData();
        AttackComponent attackComponent = swordEntity.requireComponent(AttackComponent.class);

        Entity parentEntity = swordEntity.requireComponent(ParentComponent.class).getParent();

        Vector2 collisionDirection = Pools.vector2s.grabNew();
        collisionDirection.set(enemyEntity.requireComponent(PositionComponent.class).getPosition());
        collisionDirection.sub(parentEntity.requireComponent(PositionComponent.class).getPosition());
        collisionDirection.nor();
        healthComponent.takeDamage(collisionDirection, attackComponent.getStrength());

        // Knock back the player in the opposite direction that the enemy is flung away
        KnockbackComponent playerKnockback = parentEntity.requireComponent(KnockbackComponent.class);
        playerKnockback.knockback(collisionDirection.scl(-1f), 0.8f);

        Pools.vector2s.free(collisionDirection);
    }
}
