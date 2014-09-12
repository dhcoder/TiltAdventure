package dhcoder.libgdx.collision;

import dhcoder.libgdx.collision.shape.Circle;
import dhcoder.libgdx.collision.shape.Rectangle;
import org.junit.Test;

import static dhcoder.libgdx.collision.shape.ShapeUtils.testIntersection;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

public final class CollisionSystemTest {

    private class TestCollisionListener implements CollisionListener {

        private int collisionCount;

        @Override
        public void onCollided(final Collision collision) {
            collisionCount++;
        }

        @Override
        public void onOverlapping(final Collision collision) {}

        @Override
        public void onSeparated(final Collision collision) {}

        @Override
        public void onReverted(final Collision collision) {}

        public int getCollisionCount() {
            return collisionCount;
        }
    }

    private static final int GROUP_A = 1 << 0;
    private static final int GROUP_B = 1 << 4; // Skip 1 << 1, 1 << 2, ..., for bitmask testing purposes

    @Test
    public void defaultCollidersAreInactiveAndWontCollide() {
        CollisionSystem collisionSystem = new CollisionSystem(2);
        TestCollisionListener listenerA = new TestCollisionListener();
        TestCollisionListener listenerB = new TestCollisionListener();
        collisionSystem.registerCollidesWith(GROUP_A, GROUP_B);
        collisionSystem.registerCollidesWith(GROUP_B, GROUP_A);
        Collider colliderA = collisionSystem.registerShape(GROUP_A, new Circle(4), listenerA);
        Collider colliderB = collisionSystem.registerShape(GROUP_B, new Circle(5), listenerB);

        // First, affirm these shapes would obviously collide being at the origin
        assertThat(testIntersection(new Circle(4), 0, 0, new Circle(5), 0, 0), equalTo(true));

        // ... but they won't collide, because they're inactive
        assertThat(colliderA.isActive(), equalTo(false));
        assertThat(colliderB.isActive(), equalTo(false));
        assertThat(listenerA.getCollisionCount(), equalTo(0));
        assertThat(listenerB.getCollisionCount(), equalTo(0));
        collisionSystem.triggerCollisions();
        assertThat(listenerA.getCollisionCount(), equalTo(0));
        assertThat(listenerB.getCollisionCount(), equalTo(0));

        // The objects still won't collide if one is active and the other is inactive
        colliderA.updatePosition(0f, 0f);
        assertThat(colliderA.isActive(), equalTo(true));
        assertThat(colliderB.isActive(), equalTo(false));
        collisionSystem.triggerCollisions();
        assertThat(listenerA.getCollisionCount(), equalTo(0));
        assertThat(listenerB.getCollisionCount(), equalTo(0));

        // Sanity check - collisions work as expected when both colliders are active
        colliderB.updatePosition(0f, 0f);
        assertThat(colliderA.isActive(), equalTo(true));
        assertThat(colliderB.isActive(), equalTo(true));
        collisionSystem.triggerCollisions();
        assertThat(listenerA.getCollisionCount(), equalTo(1));
        assertThat(listenerB.getCollisionCount(), equalTo(1));
    }

    @Test
    public void collisionManagerReportsCollisionBetweenCircleAndCircle() {
        CollisionSystem collisionSystem = new CollisionSystem(2);
        TestCollisionListener listenerA = new TestCollisionListener();
        TestCollisionListener listenerB = new TestCollisionListener();

        collisionSystem.registerCollidesWith(GROUP_A, GROUP_B);
        Collider colliderA = collisionSystem.registerShape(GROUP_A, new Circle(4), listenerA);
        Collider colliderB = collisionSystem.registerShape(GROUP_B, new Circle(5), listenerB);
        colliderA.updatePosition(-5, 0);
        colliderB.updatePosition(5, 0);

        collisionSystem.triggerCollisions();
        assertThat(listenerA.getCollisionCount(), equalTo(0));
        assertThat(listenerB.getCollisionCount(), equalTo(0));

        colliderA.updatePosition(2f, 1f);
        collisionSystem.triggerCollisions();
        assertThat(listenerA.getCollisionCount(), equalTo(1));
        assertThat(listenerB.getCollisionCount(), equalTo(0)); // Group B doesn't collide with Group A
    }

    @Test
    public void collisionManagerReportsCollisionBetweenRectangleAndRectangle() {
        CollisionSystem collisionSystem = new CollisionSystem(2);
        TestCollisionListener listenerA = new TestCollisionListener();
        TestCollisionListener listenerB = new TestCollisionListener();
        collisionSystem.registerCollidesWith(GROUP_A, GROUP_B);
        Collider colliderA = collisionSystem.registerShape(GROUP_A, new Rectangle(4, 3), listenerA);
        Collider colliderB = collisionSystem.registerShape(GROUP_B, new Rectangle(3, 4), listenerB);
        colliderA.updatePosition(-5, 0);
        colliderB.updatePosition(5, 0);

        collisionSystem.triggerCollisions();
        assertThat(listenerA.getCollisionCount(), equalTo(0));
        assertThat(listenerB.getCollisionCount(), equalTo(0));

        colliderA.updatePosition(4f, 1f);
        collisionSystem.triggerCollisions();
        assertThat(listenerA.getCollisionCount(), equalTo(1));
        assertThat(listenerB.getCollisionCount(), equalTo(0)); // Group B doesn't collide with Group A
    }

    @Test
    public void collisionManagerReportsCollisionBetweenCircleAndRectangle() {
        CollisionSystem collisionSystem = new CollisionSystem(2);
        TestCollisionListener listenerA = new TestCollisionListener();
        TestCollisionListener listenerB = new TestCollisionListener();
        collisionSystem.registerCollidesWith(GROUP_A, GROUP_B);
        collisionSystem.registerCollidesWith(GROUP_B, GROUP_A);
        Collider colliderA = collisionSystem.registerShape(GROUP_A, new Circle(5), listenerA);
        Collider colliderB = collisionSystem.registerShape(GROUP_B, new Rectangle(3, 4), listenerB);
        colliderA.updatePosition(-5, 0);
        colliderB.updatePosition(5, 0);

        collisionSystem.triggerCollisions();
        assertThat(listenerA.getCollisionCount(), equalTo(0));
        assertThat(listenerB.getCollisionCount(), equalTo(0));

        colliderA.updatePosition(3f, 1f);
        collisionSystem.triggerCollisions();
        assertThat(listenerA.getCollisionCount(), equalTo(1));
        assertThat(listenerB.getCollisionCount(), equalTo(1));
    }

    @Test
    public void collisionGroupsCanCollideWithThemselves() {
        CollisionSystem collisionSystem = new CollisionSystem(2);
        TestCollisionListener listenerA = new TestCollisionListener();
        TestCollisionListener listenerB = new TestCollisionListener();
        collisionSystem.registerCollidesWith(GROUP_A, GROUP_A);
        Collider colliderA = collisionSystem.registerShape(GROUP_A, new Circle(5), listenerA);
        Collider colliderB = collisionSystem.registerShape(GROUP_A, new Circle(3), listenerB);
        colliderA.updatePosition(0, 0);
        colliderB.updatePosition(0, 0);

        collisionSystem.triggerCollisions();
        assertThat(listenerA.getCollisionCount(), equalTo(1));
        assertThat(listenerB.getCollisionCount(), equalTo(1));
    }

}