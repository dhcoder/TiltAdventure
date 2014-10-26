package tiltadv.components.collision;

import com.badlogic.gdx.math.Vector2;
import dhcoder.libgdx.collision.Collision;
import dhcoder.libgdx.collision.CollisionSystem;
import dhcoder.libgdx.entity.Entity;
import tiltadv.components.body.PositionComponent;
import tiltadv.components.combat.AttackComponent;
import tiltadv.components.combat.HealthComponent;
import tiltadv.components.combat.KnockbackComponent;
import tiltadv.components.hierarchy.ParentComponent;
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
    private Entity owner;

    public SwordCollisionComponent() {
        super(Group.PLAYER_SWORD);
    }

    @Override
    protected void handleInitialize(final Entity owner) {
        this.owner = owner;
        attackComponent = owner.requireComponent(AttackComponent.class);
    }

    @Override
    protected void handleCollided(final Collision collision) {
        if (collision.getTarget().getGroupId() == Group.ENEMY) {
            handleEnemyCollision(collision);
        }
    }

    @Override
    protected void handleReset() {
        owner = null;
        attackComponent = null;
    }

    private void handleEnemyCollision(final Collision collision) {
        Entity enemyEntity = (Entity)collision.getTarget().getTag().getValue();
        HealthComponent healthComponent = enemyEntity.requireComponent(HealthComponent.class);
        if (!healthComponent.canTakeDamage()) {
            return;
        }

        Vector2 collisionDirection = Pools.vector2s.grabNew();
        Entity parentEntity = owner.requireComponent(ParentComponent.class).getParent();
        collisionDirection.set(enemyEntity.requireComponent(PositionComponent.class).getPosition());
        collisionDirection.sub(parentEntity.requireComponent(PositionComponent.class).getPosition());
        collisionDirection.nor();
        healthComponent.takeDamage(collisionDirection, attackComponent.getStrength());

        // Knock back the player a little in the opposite direction that the enemy is flung away
        KnockbackComponent playerKnockback = parentEntity.requireComponent(KnockbackComponent.class);
        playerKnockback.knockback(collisionDirection.scl(-1f, -1f), .1f);

        Pools.vector2s.free(collisionDirection);

    }
}
