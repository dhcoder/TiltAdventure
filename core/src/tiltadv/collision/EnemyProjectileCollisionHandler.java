package tiltadv.collision;

import com.badlogic.gdx.physics.box2d.Body;
import dhcoder.libgdx.entity.Entity;
import dhcoder.libgdx.physics.AbstractCollisionHandler;

/**
 * TODO: HEADER COMMENT HERE.
 */
public final class EnemyProjectileCollisionHandler extends AbstractCollisionHandler {
    @Override
    public void onCollided(final Body bodyA, final Body bodyB) {
        super.onCollided(bodyA, bodyB);
        Entity projectile = (Entity)bodyA.getUserData();
        projectile.getManager().freeEntity(projectile);
    }
}
