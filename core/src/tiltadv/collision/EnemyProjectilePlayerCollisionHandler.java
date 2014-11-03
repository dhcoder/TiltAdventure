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
 * Handler for when a projectile hits a player.
 */
public final class EnemyProjectilePlayerCollisionHandler extends AbstractCollisionHandler {
    @Override
    public void onCollided(final Body bodyA, final Body bodyB) {
        Entity projectileEntity = (Entity)bodyA.getUserData();
        Entity playerEntity = (Entity)bodyB.getUserData();

        AttackComponent attackComponent = projectileEntity.requireComponent(AttackComponent.class);
        HealthComponent healthComponent = playerEntity.requireComponent(HealthComponent.class);

        final int mark = Pools.vector2s.mark();
        Vector2 projectilePosition = Pools.vector2s.grabNew();
        Vector2 playerPosition = Pools.vector2s.grabNew();

        Physics.toPixels(projectilePosition.set(bodyA.getPosition()));
        Physics.toPixels(playerPosition.set(bodyB.getPosition()));

        Vector2 collisionDirection = Pools.vector2s.grabNew();
        collisionDirection.set(playerPosition).sub(projectilePosition).nor();
        healthComponent.takeDamage(collisionDirection, attackComponent.getStrength());
        Pools.vector2s.freeToMark(mark);
    }
}
