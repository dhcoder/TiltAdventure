//package tiltadv.components.collision;
//
//import dhcoder.libgdx.collision.Collision;
//import dhcoder.libgdx.collision.CollisionSystem;
//import dhcoder.libgdx.entity.Entity;
//import tiltadv.components.behavior.SwordBehaviorComponent;
//import tiltadv.components.combat.HealthComponent;
//import tiltadv.components.hierarchy.ParentComponent;
//import tiltadv.components.hierarchy.children.PlayerChildrenComponent;
//import tiltadv.globals.Category;
//import tiltadv.globals.Services;
//
///**
// * Component that maintains the collision logic for the main player's avatar.
// */
//public final class PlayerSensorCollisionComponent extends CollisionComponent {
//
//    static {
//        CollisionSystem collisionSystem = Services.get(CollisionSystem.class);
//        collisionSystem.registerCollidesWith(Category.PLAYER_SENSOR, Category.ENEMY);
//    }
//    private SwordBehaviorComponent swordBehaviorComponent;
//
//    public PlayerSensorCollisionComponent() {
//        super(Category.PLAYER_SENSOR);
//    }
//
//    @Override
//    protected void handleInitialize(final Entity owner) {
//        Entity playerEntity = owner.requireComponent(ParentComponent.class).getParent();
//        Entity swordEntity = playerEntity.requireComponent(PlayerChildrenComponent.class).getSwordEntity();
//        swordBehaviorComponent = swordEntity.requireComponent(SwordBehaviorComponent.class);
//    }
//
//    @Override
//    protected void handleCollided(final Collision collision) {
//        if (collision.getTarget().getGroupId() == Category.ENEMY) {
//            handleEnemyCollision(collision);
//        }
//    }
//
//    @Override
//    protected void handleOverlapping(final Collision collision) {
//        if (collision.getTarget().getGroupId() == Category.ENEMY) {
//            handleEnemyCollision(collision);
//        }
//    }
//
//    @Override
//    protected void handleReset() {
//        swordBehaviorComponent = null;
//    }
//
//    private void handleEnemyCollision(final Collision collision) {
//        Entity enemyEntity = (Entity)collision.getTarget().getTag().getValue();
//        final HealthComponent healthComponent = enemyEntity.requireComponent(HealthComponent.class);
//        if (healthComponent.canTakeDamage()) {
//            swordBehaviorComponent.swing();
//        }
//    }
//}
