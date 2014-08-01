package dhcoder.support.collision;

import dhcoder.support.collision.shape.Circle;
import dhcoder.support.collision.shape.Rectangle;
import dhcoder.support.event.EventListener;
import org.junit.Test;

import static dhcoder.support.utils.ShapeUtils.testIntersection;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

public final class CollisionSystemTest {

    private class CollisionListener implements EventListener {

        private int collisionCount;

        public int getCollisionCount() {
            return collisionCount;
        }

        @Override
        public void run(final Object sender) {
            collisionCount++;
        }
    }

    private static final int GROUP_A = 1 << 0;
    private static final int GROUP_B = 1 << 4; // Skip 1 << 1, 1 << 2, ..., for bitmask testing purposes

    @Test
    public void defaultCollidersAreInactiveAndWontCollide() {
        CollisionSystem collisionSystem = new CollisionSystem(2);
        collisionSystem.registerCollidesWith(GROUP_A, GROUP_B);
        Collider colliderA = collisionSystem.registerShape(GROUP_A, new Circle(4));
        Collider colliderB = collisionSystem.registerShape(GROUP_B, new Circle(5));

        CollisionListener listenerA = new CollisionListener();
        CollisionListener listenerB = new CollisionListener();
        colliderA.onCollision.addListener(listenerA);
        colliderB.onCollision.addListener(listenerB);

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
    }

    @Test
    public void collisionManagerReportsCollisionBetweenCircleAndCircle() {
        CollisionSystem collisionSystem = new CollisionSystem(2);
        collisionSystem.registerCollidesWith(GROUP_A, GROUP_B);
        Collider colliderA = collisionSystem.registerShape(GROUP_A, new Circle(4));
        Collider colliderB = collisionSystem.registerShape(GROUP_B, new Circle(5));
        colliderA.updatePosition(-5, 0);
        colliderB.updatePosition(5, 0);

        CollisionListener listenerA = new CollisionListener();
        CollisionListener listenerB = new CollisionListener();
        colliderA.onCollision.addListener(listenerA);
        colliderB.onCollision.addListener(listenerB);

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
        collisionSystem.registerCollidesWith(GROUP_A, GROUP_B);
        Collider colliderA = collisionSystem.registerShape(GROUP_A, new Rectangle(4, 3));
        Collider colliderB = collisionSystem.registerShape(GROUP_B, new Rectangle(3, 4));
        colliderA.updatePosition(-5, 0);
        colliderB.updatePosition(5, 0);

        CollisionListener listenerA = new CollisionListener();
        CollisionListener listenerB = new CollisionListener();
        colliderA.onCollision.addListener(listenerA);
        colliderB.onCollision.addListener(listenerB);

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
        collisionSystem.registerCollidesWith(GROUP_A, GROUP_B);
        collisionSystem.registerCollidesWith(GROUP_B, GROUP_A);
        Collider colliderA = collisionSystem.registerShape(GROUP_A, new Circle(5));
        Collider colliderB = collisionSystem.registerShape(GROUP_B, new Rectangle(3, 4));
        colliderA.updatePosition(-5, 0);
        colliderB.updatePosition(5, 0);

        CollisionListener listenerA = new CollisionListener();
        CollisionListener listenerB = new CollisionListener();
        colliderA.onCollision.addListener(listenerA);
        colliderB.onCollision.addListener(listenerB);

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
        collisionSystem.registerCollidesWith(GROUP_A, GROUP_A);
        Collider colliderA = collisionSystem.registerShape(GROUP_A, new Circle(5));
        Collider colliderB = collisionSystem.registerShape(GROUP_A, new Circle(3));
        colliderA.updatePosition(0, 0);
        colliderB.updatePosition(0, 0);

        CollisionListener listenerA = new CollisionListener();
        CollisionListener listenerB = new CollisionListener();
        colliderA.onCollision.addListener(listenerA);
        colliderB.onCollision.addListener(listenerB);

        collisionSystem.triggerCollisions();
        assertThat(listenerA.getCollisionCount(), equalTo(1));
        assertThat(listenerB.getCollisionCount(), equalTo(1));
    }

}