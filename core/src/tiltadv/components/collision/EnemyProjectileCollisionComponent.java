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
//public final class EnemyProjectileCollisionComponent extends CollisionComponent {
//
//    static {
//        CollisionSystem collisionSystem = Services.get(CollisionSystem.class);
//        collisionSystem.registerCollidesWith(Category.ENEMY_PROJECTILE, Category.OBSTACLES | Category.PLAYER);
//    }
//
//    private AttackComponent attackComponent;
//
//    public EnemyProjectileCollisionComponent() {
//        super(Category.ENEMY_PROJECTILE);
//    }
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
//    @Override
//    protected void handleCollided(final Collision collision) {
//
//        if (collision.getTarget().getGroupId() == Category.PLAYER) {
//            Entity playerEntity = (Entity)collision.getTarget().getTag().getValue();
//            HealthComponent healthComponent = playerEntity.requireComponent(HealthComponent.class);
//
//            Vector2 collisionDirection = Pools.vector2s.grabNew();
//            collisionDirection.set(collision.getTarget().getCurrPosition()).sub(collision.getSource().getCurrPosition())
//                .nor();
//            healthComponent.takeDamage(collisionDirection, attackComponent.getStrength());
//            Pools.vector2s.free(collisionDirection);
//        }
//
//        // Projectile dies on collision
//        Entity sourceEntity = (Entity)collision.getSource().getTag().getValue();
//        sourceEntity.getManager().freeEntity(sourceEntity);
//    }
//}
