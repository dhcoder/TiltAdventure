package dhcoder.libgdx.physics;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Box2D;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import dhcoder.support.memory.HeapPool;
import dhcoder.support.memory.Pool;
import dhcoder.support.memory.Poolable;
import dhcoder.support.time.Duration;

import java.util.List;

/**
 * Constants for our physics system
 */
public final class PhysicsSystem {

    private interface CollisionCallback {
        public void run(CollisionCallbackData callbackData);
    }

    private static final class CollisionHandlerEntry {
        int categoryBitsFirst;
        int categoryBitsSecond;
        CollisionHandler collisionHandler;

        public CollisionHandlerEntry(final int categoryBitsFirst, final int categoryBitsSecond,
            final CollisionHandler collisionHandler) {
            this.categoryBitsFirst = categoryBitsFirst;
            this.categoryBitsSecond = categoryBitsSecond;
            this.collisionHandler = collisionHandler;
        }

        public boolean matches(final int categoryA, final int categoryB) {
            return (((this.categoryBitsFirst & categoryA) != 0 && (this.categoryBitsSecond & categoryB) != 0) ||
                ((this.categoryBitsFirst & categoryB) != 0 && (this.categoryBitsSecond & categoryA) != 0));
        }

        public boolean isFirstCategory(final int categoryBitsA) {
            return categoryBitsFirst == categoryBitsA;
        }
    }

    private static final class CollisionFixtures implements Poolable {
        public Fixture fixtureA;
        public Fixture fixtureB;

        public boolean matches(final Fixture fixtureC, final Fixture fixtureD) {
            return ((fixtureA == fixtureC && fixtureB == fixtureD) ||
                (fixtureA == fixtureD && fixtureB == fixtureC));
        }

        @Override
        public void reset() {
            fixtureA = null;
            fixtureB = null;
        }
    }

    private static final class CollisionCallbackData implements Poolable {
        // Order matters with callbacks! Users expect one body to appear first and another
        // to appear second (depending on how they registered their callback)
        public Body bodyFirst;
        public Body bodySecond;
        public CollisionHandler collisionHandler;

        @Override
        public void reset() {
            bodyFirst = null;
            bodySecond = null;
            collisionHandler = null;
        }
    }

    private final class CollisionListener implements ContactListener {

        @Override
        public void beginContact(final Contact contact) {
            if (!contact.isTouching()) {
                return;
            }

            if (runCollisionHandlers(contact, callCollidedHandler)) {
                final CollisionFixtures collisionFixtures = collisionsPool.grabNew();
                collisionFixtures.fixtureA = contact.getFixtureA();
                collisionFixtures.fixtureB = contact.getFixtureB();
            }
        }

        @Override
        public void endContact(final Contact contact) {
            if (runCollisionHandlers(contact, callSeparatedHandler)) {
                final List<CollisionFixtures> collisions = collisionsPool.getItemsInUse();
                int numCollisions = collisions.size();
                for (int i = 0; i < numCollisions; i++) {
                    CollisionFixtures collisionFixtures = collisions.get(i);
                    if (collisionFixtures.matches(contact.getFixtureA(), contact.getFixtureB())) {
                        collisionsPool.free(collisionFixtures);
                        break;
                    }
                }
            }
        }

        @Override
        public void preSolve(final Contact contact, final Manifold oldManifold) {

            if (hasCollisionHandlers(contact)) {
                contact.setEnabled(false); // Collision will be handled externally, don't handle it via Box2D
            }
        }

        @Override
        public void postSolve(final Contact contact, final ContactImpulse impulse) {}
    }

    // Recommended values from Box2D manual
    private static final int VELOCITY_ITERATIONS = 6;
    private static final int POSITION_ITERATIONS = 2;
    private final CollisionCallback callCollidedHandler = new CollisionCallback() {
        @Override
        public void run(final CollisionCallbackData data) {
            data.collisionHandler.onCollided(data.bodyFirst, data.bodySecond);
        }
    };
    private final CollisionCallback callOverlappingHandler = new CollisionCallback() {
        @Override
        public void run(final CollisionCallbackData data) {
            data.collisionHandler.onOverlapping(data.bodyFirst, data.bodySecond);
        }
    };
    private final CollisionCallback callSeparatedHandler = new CollisionCallback() {
        @Override
        public void run(final CollisionCallbackData data) {
            data.collisionHandler.onSeparated(data.bodyFirst, data.bodySecond);
        }
    };
    private final World world;
    private final Array<PhysicsElement> physicsElements;
    private final Array<CollisionHandlerEntry> collisionHandlers;
    private final HeapPool<CollisionFixtures> collisionsPool;
    private final Pool<CollisionCallbackData> collisionDataPool = Pool.of(CollisionCallbackData.class, 1);
    private Box2DDebugRenderer collisionRenderer;
    private Matrix4 debugRenderMatrix;

    static {
        Box2D.init();
    }

    public PhysicsSystem(final int capacity) {
        this.world = new World(new Vector2(0f, 0f), true);
        world.setContactListener(new CollisionListener());

        physicsElements = new Array<PhysicsElement>(false, capacity);
        collisionHandlers = new Array<CollisionHandlerEntry>();
        collisionsPool = HeapPool.of(CollisionFixtures.class, capacity);
    }

    public World getWorld() {
        return world;
    }

    public void addElement(final PhysicsElement physicsElement) {
        physicsElements.add(physicsElement);
    }

    public boolean removeElement(final PhysicsElement physicsElement) {
        return physicsElements.removeValue(physicsElement, true);
    }

    /**
     * Register a {@link CollisionHandler} with this physics system. Note this either a registered handler will handle
     * a collision OR Box2D will handle it, but not both. The category order that a handler is registered with will
     * be preserved when the handler is called.
     * <p/>
     * You can register multiple handlers for the same collision, which is useful if you have a default behavior you
     * want to happen in multiple collision cases.
     */
    public void addCollisionHandler(final int categoryBitsA, final int categoryBitsB,
        final CollisionHandler collisionHandler) {
        collisionHandlers.add(new CollisionHandlerEntry(categoryBitsA, categoryBitsB, collisionHandler));
    }

    public void update(final Duration elapsedTime) {

        // Variable time step is not recommended, but we'll be careful... We can change this later if it causes
        // trouble, but otherwise, it would be nice to have 1:1 entity::update and physics::update steps.
        world.step(elapsedTime.getSeconds(), VELOCITY_ITERATIONS, POSITION_ITERATIONS);

        int numCollisionFixtures = collisionsPool.getItemsInUse().size();
        for (int i = 0; i < numCollisionFixtures; i++) {
            CollisionFixtures collisionFixtures = collisionsPool.getItemsInUse().get(i);
            runCollisionHandlers(collisionFixtures.fixtureA, collisionFixtures.fixtureB, callOverlappingHandler);
        }

        for (int i = 0; i < physicsElements.size; i++) {
            PhysicsElement physicsElement = physicsElements.get(i);
            physicsElement.syncWithPhysics();
        }
    }

    public void debugRender(final Matrix4 cameraMatrix, final float pixelsToMeters) {
        if (collisionRenderer == null) {
            collisionRenderer = new Box2DDebugRenderer();
            debugRenderMatrix = new Matrix4();
        }

        debugRenderMatrix.set(cameraMatrix).scl(pixelsToMeters);
        collisionRenderer.render(world, debugRenderMatrix);
    }

    public void dispose() {
        if (collisionRenderer != null) {
            collisionRenderer.dispose();
        }
        world.dispose();
    }

    private boolean hasCollisionHandlers(final Contact contact) {
        Fixture fixtureA = contact.getFixtureA();
        Fixture fixtureB = contact.getFixtureB();

        for (int i = 0; i < collisionHandlers.size; i++) {
            CollisionHandlerEntry entry = collisionHandlers.get(i);
            if (entry.matches(fixtureA.getFilterData().categoryBits, fixtureB.getFilterData().categoryBits)) {
                return true;
            }
        }

        return false;
    }

    private boolean runCollisionHandlers(final Contact contact, final CollisionCallback collisionCallback) {
        Fixture fixtureA = contact.getFixtureA();
        Fixture fixtureB = contact.getFixtureB();

        return runCollisionHandlers(fixtureA, fixtureB, collisionCallback);
    }

    /**
     * Given two fixtures that are colliding, call any collision handlers that may have been registered to handle it.
     *
     * Returns true if a callback was registered (and therefore called).
     */
    private boolean runCollisionHandlers(final Fixture fixtureA, final Fixture fixtureB,
        final CollisionCallback collisionCallback) {

        boolean triggeredCallback = false;

        for (int i = 0; i < collisionHandlers.size; i++) {
            CollisionHandlerEntry entry = collisionHandlers.get(i);
            if (entry.matches(fixtureA.getFilterData().categoryBits, fixtureB.getFilterData().categoryBits)) {
                Body bodyA = fixtureA.getBody();
                Body bodyB = fixtureB.getBody();

                final CollisionCallbackData data = collisionDataPool.grabNew();
                if (entry.isFirstCategory(fixtureA.getFilterData().categoryBits)) {
                    data.bodyFirst = bodyA;
                    data.bodySecond = bodyB;
                }
                else {
                    data.bodyFirst = bodyB;
                    data.bodySecond = bodyA;
                }
                data.collisionHandler = entry.collisionHandler;
                collisionCallback.run(data);
                collisionDataPool.freeCount(1);
                triggeredCallback = true;
            }
        }

        return triggeredCallback;
    }
}
