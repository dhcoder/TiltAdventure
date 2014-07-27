package dhcoder.support.collision;

import dhcoder.support.event.EventHandler;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

public final class CollisionManagerTest {

    private class CollisionHandler implements EventHandler {

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
        CollisionHandle handleA = collisionManager.registerCircle(GROUP_A, -5, 0, 4);
        CollisionHandle handleB = collisionManager.registerCircle(GROUP_B, 5, 0, 4);
        CollisionHandler handlerA = new CollisionHandler();
        CollisionHandler handlerB = new CollisionHandler();
        handleA.onCollision.addHandler(handlerA);
        handleB.onCollision.addHandler(handlerB);

        collisionManager.triggerCollisions();
        assertThat(handlerA.getCollisionCount(), equalTo(0));
        assertThat(handlerB.getCollisionCount(), equalTo(0));

        handleA.updateOrigin(2f, 1f);
        collisionManager.triggerCollisions();
        assertThat(handlerA.getCollisionCount(), equalTo(1));
        assertThat(handlerB.getCollisionCount(), equalTo(0)); // Group B doesn't collide with Group A
    }

    @Test
    public void collisionManagerReportsCollisionBetweenRectangleAndRectangle() {
        CollisionManager collisionManager = new CollisionManager(2);
        collisionManager.registerCollidesWith(GROUP_A, GROUP_B);
        CollisionHandle handleA = collisionManager.registerRectangle(GROUP_A, -5, 0, 4, 3);
        CollisionHandle handleB = collisionManager.registerRectangle(GROUP_B, 5, 0, 3, 4);
        CollisionHandler handlerA = new CollisionHandler();
        CollisionHandler handlerB = new CollisionHandler();
        handleA.onCollision.addHandler(handlerA);
        handleB.onCollision.addHandler(handlerB);

        collisionManager.triggerCollisions();
        assertThat(handlerA.getCollisionCount(), equalTo(0));
        assertThat(handlerB.getCollisionCount(), equalTo(0));

        handleA.updateOrigin(4f, 1f);
        collisionManager.triggerCollisions();
        assertThat(handlerA.getCollisionCount(), equalTo(1));
        assertThat(handlerB.getCollisionCount(), equalTo(0)); // Group B doesn't collide with Group A
    }

    @Test
    public void collisionManagerReportsCollisionBetweenCircleAndRectangle() {
        CollisionManager collisionManager = new CollisionManager(2);
        collisionManager.registerCollidesWith(GROUP_A, GROUP_B);
        collisionManager.registerCollidesWith(GROUP_B, GROUP_A);
        CollisionHandle handleA = collisionManager.registerCircle(GROUP_A, -5, 0, 5);
        CollisionHandle handleB = collisionManager.registerRectangle(GROUP_B, 5, 0, 3, 4);
        CollisionHandler handlerA = new CollisionHandler();
        CollisionHandler handlerB = new CollisionHandler();
        handleA.onCollision.addHandler(handlerA);
        handleB.onCollision.addHandler(handlerB);

        collisionManager.triggerCollisions();
        assertThat(handlerA.getCollisionCount(), equalTo(0));
        assertThat(handlerB.getCollisionCount(), equalTo(0));

        handleA.updateOrigin(3f, 1f);
        collisionManager.triggerCollisions();
        assertThat(handlerA.getCollisionCount(), equalTo(1));
        assertThat(handlerB.getCollisionCount(), equalTo(1));
    }

    @Test
    public void collisionManagerReportsCollisionsWithinTheSameGroup() {
        CollisionManager collisionManager = new CollisionManager(2);
        collisionManager.registerCollidesWith(GROUP_A, GROUP_A);
        CollisionHandle handleA = collisionManager.registerCircle(GROUP_A, -5, 0, 5);
        CollisionHandle handleB = collisionManager.registerCircle(GROUP_A, -3, 0, 5);
        CollisionHandler handlerA = new CollisionHandler();
        CollisionHandler handlerB = new CollisionHandler();
        handleA.onCollision.addHandler(handlerA);
        handleB.onCollision.addHandler(handlerB);

        collisionManager.triggerCollisions();
        assertThat(handlerA.getCollisionCount(), equalTo(1));
        assertThat(handlerB.getCollisionCount(), equalTo(1));
    }

}