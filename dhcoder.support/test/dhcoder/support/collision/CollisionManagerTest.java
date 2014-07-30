package dhcoder.support.collision;

import dhcoder.support.collision.shape.Circle;
import dhcoder.support.collision.shape.Rectangle;
import dhcoder.support.event.EventListener;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

public final class CollisionManagerTest {

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
    public void collisionManagerReportsCollisionBetweenCircleAndCircle() {
        CollisionManager collisionManager = new CollisionManager(2);
        collisionManager.registerCollidesWith(GROUP_A, GROUP_B);
        CollisionHandle handleA = collisionManager.registerShape(GROUP_A, new Circle(-5, 0, 4));
        CollisionHandle handleB = collisionManager.registerShape(GROUP_B, new Circle(5, 0, 4));
        CollisionListener listenerA = new CollisionListener();
        CollisionListener listenerB = new CollisionListener();
        handleA.onCollision.addListener(listenerA);
        handleB.onCollision.addListener(listenerB);

        collisionManager.triggerCollisions();
        assertThat(listenerA.getCollisionCount(), equalTo(0));
        assertThat(listenerB.getCollisionCount(), equalTo(0));

        handleA.updateOrigin(2f, 1f);
        collisionManager.triggerCollisions();
        assertThat(listenerA.getCollisionCount(), equalTo(1));
        assertThat(listenerB.getCollisionCount(), equalTo(0)); // Group B doesn't collide with Group A
    }

    @Test
    public void collisionManagerReportsCollisionBetweenRectangleAndRectangle() {
        CollisionManager collisionManager = new CollisionManager(2);
        collisionManager.registerCollidesWith(GROUP_A, GROUP_B);
        CollisionHandle handleA = collisionManager.registerShape(GROUP_A, new Rectangle(-5, 0, 4, 3));
        CollisionHandle handleB = collisionManager.registerShape(GROUP_B, new Rectangle(5, 0, 3, 4));
        CollisionListener listenerA = new CollisionListener();
        CollisionListener listenerB = new CollisionListener();
        handleA.onCollision.addListener(listenerA);
        handleB.onCollision.addListener(listenerB);

        collisionManager.triggerCollisions();
        assertThat(listenerA.getCollisionCount(), equalTo(0));
        assertThat(listenerB.getCollisionCount(), equalTo(0));

        handleA.updateOrigin(4f, 1f);
        collisionManager.triggerCollisions();
        assertThat(listenerA.getCollisionCount(), equalTo(1));
        assertThat(listenerB.getCollisionCount(), equalTo(0)); // Group B doesn't collide with Group A
    }

    @Test
    public void collisionManagerReportsCollisionBetweenCircleAndRectangle() {
        CollisionManager collisionManager = new CollisionManager(2);
        collisionManager.registerCollidesWith(GROUP_A, GROUP_B);
        collisionManager.registerCollidesWith(GROUP_B, GROUP_A);
        CollisionHandle handleA = collisionManager.registerShape(GROUP_A, new Circle(-5, 0, 5));
        CollisionHandle handleB = collisionManager.registerShape(GROUP_B, new Rectangle(5, 0, 3, 4));
        CollisionListener listenerA = new CollisionListener();
        CollisionListener listenerB = new CollisionListener();
        handleA.onCollision.addListener(listenerA);
        handleB.onCollision.addListener(listenerB);

        collisionManager.triggerCollisions();
        assertThat(listenerA.getCollisionCount(), equalTo(0));
        assertThat(listenerB.getCollisionCount(), equalTo(0));

        handleA.updateOrigin(3f, 1f);
        collisionManager.triggerCollisions();
        assertThat(listenerA.getCollisionCount(), equalTo(1));
        assertThat(listenerB.getCollisionCount(), equalTo(1));
    }

    @Test
    public void collisionManagerReportsCollisionsWithinTheSameGroup() {
        CollisionManager collisionManager = new CollisionManager(2);
        collisionManager.registerCollidesWith(GROUP_A, GROUP_A);
        CollisionHandle handleA = collisionManager.registerShape(GROUP_A, new Circle(-5, 0, 5));
        CollisionHandle handleB = collisionManager.registerShape(GROUP_A, new Circle(-3, 0, 5));
        CollisionListener listenerA = new CollisionListener();
        CollisionListener listenerB = new CollisionListener();
        handleA.onCollision.addListener(listenerA);
        handleB.onCollision.addListener(listenerB);

        collisionManager.triggerCollisions();
        assertThat(listenerA.getCollisionCount(), equalTo(1));
        assertThat(listenerB.getCollisionCount(), equalTo(1));
    }

}