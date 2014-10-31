//package tiltadv.components.collision;
//
//import dhcoder.libgdx.collision.Collision;
//import dhcoder.libgdx.collision.CollisionSystem;
//import tiltadv.globals.Category;
//import tiltadv.globals.Services;
//
///**
// * Component that maintains the collision logic for the main player's avatar.
// */
//public final class PlayerCollisionComponent extends CollisionComponent {
//
//    static {
//        CollisionSystem collisionSystem = Services.get(CollisionSystem.class);
//        collisionSystem.registerCollidesWith(Category.PLAYER, Category.OBSTACLES);
//    }
//
//    public PlayerCollisionComponent() {
//        super(Category.PLAYER);
//    }
//
//    @Override
//    protected void handleCollided(final Collision collision) {
//        if (collision.getTarget().getGroupId() == Category.OBSTACLES) {
//            CollisionSystem collisionSystem = Services.get(CollisionSystem.class);
//            collisionSystem.extractSourceCollider(collision);
//        }
//    }
//}
