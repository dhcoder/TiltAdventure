package tiltadv.collision;

import com.badlogic.gdx.physics.box2d.Body;
import dhcoder.libgdx.entity.Entity;
import dhcoder.libgdx.physics.AbstractCollisionHandler;

/**
 * Default handler for an enemy projectile so it destroys itself on collision.
 */
public final class EnemyProjectileDieOnCollisionHandler extends AbstractCollisionHandler {
    @Override
    public void onCollided(final Body bodyA, final Body bodyB) {
        Entity projectile = (Entity)bodyA.getUserData();
        projectile.getManager().freeEntity(projectile);
    }
}
