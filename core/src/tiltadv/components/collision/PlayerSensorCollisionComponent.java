package tiltadv.components.collision;

import com.badlogic.gdx.math.Vector2;
import dhcoder.libgdx.collision.Collider;
import dhcoder.libgdx.collision.Collision;
import dhcoder.libgdx.collision.CollisionSystem;
import dhcoder.libgdx.entity.Entity;
import dhcoder.support.time.Duration;
import tiltadv.components.behavior.SwordBehaviorComponent;
import tiltadv.components.combat.HealthComponent;
import tiltadv.components.model.HeadingComponent;
import tiltadv.components.model.SizeComponent;
import tiltadv.globals.Group;
import tiltadv.globals.Services;
import tiltadv.memory.Pools;

import static dhcoder.support.contract.ContractUtils.requireNonNull;

/**
 * Component that maintains the collision logic for the main player's avatar.
 */
public final class PlayerSensorCollisionComponent extends CollisionComponent {

    static {
        CollisionSystem collisionSystem = Services.get(CollisionSystem.class);
        collisionSystem.registerCollidesWith(Group.PLAYER_SENSOR, Group.ENEMY);
    }

    private HeadingComponent headingComponent;
    private SizeComponent sizeComponent;

    private Entity swordEntity;
    private SwordBehaviorComponent swordBehaviorComponent;

    public PlayerSensorCollisionComponent() {
        super(Group.PLAYER_SENSOR);
    }

    public Entity getSwordEntity() {
        return swordEntity;
    }

    public PlayerSensorCollisionComponent setSwordEntity(final Entity swordEntity) {
        this.swordEntity = swordEntity;
        swordBehaviorComponent = swordEntity.requireComponent(SwordBehaviorComponent.class);
        return this;
    }

    @Override
    protected void handleInitialize(final Entity owner) {
        headingComponent = owner.requireComponent(HeadingComponent.class);
        sizeComponent = owner.requireComponent(SizeComponent.class);

        requireNonNull(swordEntity, "SwordEntity not set");
    }

    @Override
    protected void handleUpdate(final Duration elapsedTime) {
        Collider collider = getCollider();

        int mark = Pools.vector2s.mark();
        Vector2 offset = Pools.vector2s.grabNew();
        offset.set(sizeComponent.getSize().x / 2f + collider.getShape().getHalfWidth(), 0f);
        offset.rotate(headingComponent.getHeading().getDegrees());
        setOffset(offset);

        Pools.vector2s.freeToMark(mark);

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
            swordBehaviorComponent.swing();
        }
    }

    @Override
    protected void handleReset() {
        headingComponent = null;
        sizeComponent = null;

        swordEntity = null;
        swordBehaviorComponent = null;
    }
}
