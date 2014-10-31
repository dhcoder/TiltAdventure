//package tiltadv.components.collision;
//
//import com.badlogic.gdx.math.Vector2;
//import dhcoder.libgdx.collision.Collision;
//import dhcoder.libgdx.collision.CollisionSystem;
//import dhcoder.libgdx.entity.Entity;
//import tiltadv.components.combat.AttackComponent;
//import tiltadv.components.combat.HealthComponent;
//import tiltadv.globals.Category;
//import tiltadv.globals.Services;
//import tiltadv.memory.Pools;
//
///**
// * Component that maintains the collision logic for the main player's avatar.
// */
//public final class EnemyCollisionComponent extends CollisionComponent {
//
//    static {
//        CollisionSystem collisionSystem = Services.get(CollisionSystem.class);
//        collisionSystem.registerCollidesWith(Category.ENEMY, Category.OBSTACLES | Category.PLAYER);
//    }
//
//    private AttackComponent attackComponent;
//
//    @Override
//    protected void handleInitialize(final Entity owner) {
//        attackComponent = owner.requireComponent(AttackComponent.class);
//    }
//
//    @Override
//    protected void handleReset() {
//        attackComponent = null;
//    }
//
//    public EnemyCollisionComponent() {
//        super(Category.ENEMY);
//    }
//
//    @Override
//    protected void handleCollided(final Collision collision) {
//        final int groupId = collision.getTarget().getGroupId();
//        if (groupId == Category.OBSTACLES) {
//            CollisionSystem collisionSystem = Services.get(CollisionSystem.class);
//            collisionSystem.extractSourceCollider(collision);
//        }
//        else if (groupId == Category.PLAYER) {
//            handlePlayerCollision(collision);
//        }
//    }
//
//    @Override
//    protected void handleOverlapping(final Collision collision) {
//        final int groupId = collision.getTarget().getGroupId();
//        if (groupId == Category.PLAYER) {
//            handlePlayerCollision(collision);
//        }
//    }
//
//    private void handlePlayerCollision(final Collision collision) {
//        Entity playerEntity = (Entity)collision.getTarget().getTag().getValue();
//        HealthComponent playerHealth = playerEntity.requireComponent(HealthComponent.class);
//        if (!playerHealth.canTakeDamage()) {
//            return;
//        }
//
//        Vector2 collisionDirection = Pools.vector2s.grabNew();
//        collisionDirection.set(collision.getTarget().getCurrPosition()).sub(collision.getSource().getCurrPosition())
//            .nor();
//        playerHealth.takeDamage(collisionDirection, attackComponent.getStrength());
//        Pools.vector2s.free(collisionDirection);
//    }
//}
