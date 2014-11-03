package tiltadv.collision;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import dhcoder.libgdx.entity.Entity;
import dhcoder.libgdx.physics.AbstractCollisionHandler;
import tiltadv.components.combat.AttackComponent;
import tiltadv.components.combat.HealthComponent;
import tiltadv.globals.Physics;
import tiltadv.memory.Pools;

/**
 * Handler for when an enemy touches a player.
 */
public final class EnemyPlayerCollisionHandler extends AbstractCollisionHandler {
    @Override
    public void onCollided(final Body bodyA, final Body bodyB) {
        handleCollision(bodyA, bodyB);
    }

    @Override
    public void onOverlapping(final Body bodyA, final Body bodyB) {
        handleCollision(bodyA, bodyB);
    }

    public void handleCollision(final Body bodyA, final Body bodyB) {
        Entity playerEntity = (Entity)bodyB.getUserData();
        HealthComponent healthComponent = playerEntity.requireComponent(HealthComponent.class);
        if (!healthComponent.canTakeDamage()) {
            return;
        }

        Entity enemyEntity = (Entity)bodyA.getUserData();

        AttackComponent attackComponent = enemyEntity.requireComponent(AttackComponent.class);

        final int mark = Pools.vector2s.mark();
        Vector2 enemyPosition = Pools.vector2s.grabNew();
        Vector2 playerPosition = Pools.vector2s.grabNew();

        Physics.toPixels(enemyPosition.set(bodyA.getPosition()));
        Physics.toPixels(playerPosition.set(bodyB.getPosition()));

        Vector2 collisionDirection = Pools.vector2s.grabNew();
        collisionDirection.set(playerPosition).sub(enemyPosition).nor();
        healthComponent.takeDamage(collisionDirection, attackComponent.getStrength());
        Pools.vector2s.freeToMark(mark);
    }
}
