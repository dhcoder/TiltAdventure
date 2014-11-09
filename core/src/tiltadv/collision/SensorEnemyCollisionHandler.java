package tiltadv.collision;

import com.badlogic.gdx.physics.box2d.Body;
import dhcoder.libgdx.entity.Entity;
import dhcoder.libgdx.physics.AbstractCollisionHandler;
import tiltadv.components.behavior.SwordBehaviorComponent;
import tiltadv.components.hierarchy.ParentComponent;
import tiltadv.components.hierarchy.children.PlayerChildrenComponent;

/**
 * Handler for when an enemy touches a player.
 */
public final class SensorEnemyCollisionHandler extends AbstractCollisionHandler {
    @Override
    public void onCollided(final Body bodyA, final Body bodyB) {
        handleCollision(bodyA);
    }

    @Override
    public void onOverlapping(final Body bodyA, final Body bodyB) {
        handleCollision(bodyA);
    }

    public void handleCollision(final Body bodySensor) {
        Entity sensorEntity = (Entity)bodySensor.getUserData();
        Entity playerEntity = sensorEntity.requireComponent(ParentComponent.class).getParent();
        Entity swordEntity = playerEntity.requireComponent(PlayerChildrenComponent.class).getSwordEntity();
        swordEntity.requireComponent(SwordBehaviorComponent.class).swing();
    }
}
