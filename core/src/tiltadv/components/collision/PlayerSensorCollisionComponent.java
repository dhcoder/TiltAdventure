package tiltadv.components.collision;

import com.badlogic.gdx.math.Vector2;
import dhcoder.libgdx.collision.Collider;
import dhcoder.libgdx.collision.Collision;
import dhcoder.libgdx.collision.CollisionSystem;
import dhcoder.libgdx.entity.Entity;
import dhcoder.support.time.Duration;
import tiltadv.components.behavior.PlayerBehaviorComponent;
import tiltadv.components.model.HeadingComponent;
import tiltadv.components.model.SizeComponent;
import tiltadv.globals.Group;
import tiltadv.globals.Services;
import tiltadv.memory.Pools;

/**
 * Component that maintains the collision logic for the main player's avatar.
 */
public final class PlayerSensorCollisionComponent extends CollisionComponent {

    static {
        CollisionSystem collisionSystem = Services.get(CollisionSystem.class);
        collisionSystem.registerCollidesWith(Group.PLAYER_SENSOR, Group.ENEMY);
    }

    private PlayerBehaviorComponent playerBehaviorComponent;
    private HeadingComponent headingComponent;
    private SizeComponent sizeComponent;

    public PlayerSensorCollisionComponent() {
        super(Group.PLAYER_SENSOR);
    }

    @Override
    protected void handleInitialize(final Entity owner) {
        playerBehaviorComponent = owner.requireComponent(PlayerBehaviorComponent.class);
        headingComponent = owner.requireComponent(HeadingComponent.class);
        sizeComponent = owner.requireComponent(SizeComponent.class);
    }

    @Override
    protected void handleUpdate(final Duration elapsedTime) {
        Collider collider = getCollider();

        Vector2 offset = Pools.vector2s.grabNew();
        offset.set(sizeComponent.getSize().x / 2f + collider.getShape().getHalfWidth(), 0f);
        offset.rotate(headingComponent.getHeading().getDegrees());
        setOffset(offset);
        Pools.vector2s.freeCount(1);
    }

    @Override
    protected void handleReset() {
        playerBehaviorComponent = null;
        headingComponent = null;
        sizeComponent = null;
    }

    @Override
    protected void handleCollided(final Collision collision) {
        if (collision.getTarget().getGroupId() == Group.ENEMY) {
            //playerBehaviorComponent.attack();
        }
    }
}
