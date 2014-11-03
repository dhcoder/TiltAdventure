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
import dhcoder.support.memory.Pool;
import dhcoder.support.memory.Poolable;
import dhcoder.support.time.Duration;

import static dhcoder.support.text.StringUtils.format;

/**
 * Constants for our physics system
 */
public final class PhysicsSystem {

    private static final class CollisionHandlerEntry {
        int categoryFirst;
        int categorySecond;
        CollisionHandler collisionHandler;

        public CollisionHandlerEntry(final int categoryFirst, final int categorySecond,
            final CollisionHandler collisionHandler) {
            this.categoryFirst = categoryFirst;
            this.categorySecond = categorySecond;
            this.collisionHandler = collisionHandler;
        }

        public boolean matches(final int categoryA, final int categoryB) {
            return ((this.categoryFirst == categoryA && this.categorySecond == categoryB) ||
                (this.categoryFirst == categoryB && this.categorySecond == categoryA));
        }

        public boolean isFirstCategory(final int categoryA) {
            return categoryFirst == categoryA;
        }
    }

    private static final class CollisionData implements Poolable {
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

            final CollisionData collisionData = collisionDataPool.grabNew();
            if (getCollisionData(contact, collisionData)) {
                collisionData.collisionHandler.onCollided(collisionData.bodyFirst, collisionData.bodySecond);
            }
            collisionDataPool.freeCount(1);
        }

        @Override
        public void endContact(final Contact contact) {
            final CollisionData collisionData = collisionDataPool.grabNew();
            if (getCollisionData(contact, collisionData)) {
                collisionData.collisionHandler.onSeparated(collisionData.bodyFirst, collisionData.bodySecond);
            }
            collisionDataPool.freeCount(1);
        }

        @Override
        public void preSolve(final Contact contact, final Manifold oldManifold) {
            final CollisionData collisionData = collisionDataPool.grabNew();
            if (getCollisionData(contact, collisionData)) {
                collisionData.collisionHandler.onSeparated(collisionData.bodyFirst, collisionData.bodySecond);
            }
            collisionDataPool.freeCount(1);
        }

        @Override
        public void postSolve(final Contact contact, final ContactImpulse impulse) {}

        /**
         * Get the collision key associated with the various input parameters. Returns false if nothing was found
         * (which means there's no collision handler registered for the two fixtures).
         */
        private boolean getCollisionData(final Contact contact, final CollisionData outCollisionData) {
            Fixture fixtureA = contact.getFixtureA();
            Fixture fixtureB = contact.getFixtureB();

            for (int i = 0; i < collisionHandlers.size; i++) {
                CollisionHandlerEntry entry = collisionHandlers.get(i);
                if (entry.matches(fixtureA.getFilterData().categoryBits, fixtureB.getFilterData().categoryBits)) {
                    Body bodyA = fixtureA.getBody();
                    Body bodyB = fixtureB.getBody();

                    if (entry.isFirstCategory(fixtureA.getFilterData().categoryBits)) {
                        outCollisionData.bodyFirst = bodyA;
                        outCollisionData.bodySecond = bodyB;
                    }
                    else {
                        outCollisionData.bodyFirst = bodyB;
                        outCollisionData.bodySecond = bodyA;
                    }
                    outCollisionData.collisionHandler = entry.collisionHandler;
                    return true;
                }
            }
            return false;
        }
    }

    // Recommended values from Box2D manual
    private static final int VELOCITY_ITERATIONS = 6;
    private static final int POSITION_ITERATIONS = 2;
    private final World world;
    private final Array<PhysicsElement> physicsElements;
    private final Array<CollisionHandlerEntry> collisionHandlers;
    private final Pool<CollisionData> collisionDataPool = Pool.of(CollisionData.class, 1);
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

    public void addCollisionHandler(final int categoryA, final int categoryB, final CollisionHandler collisionHandler) {
        for (int i = 0; i < collisionHandlers.size; i++) {
            CollisionHandlerEntry collisionHandlerEntry = collisionHandlers.get(i);
            if (collisionHandlerEntry.matches(categoryA, categoryB)) {
                throw new IllegalArgumentException(
                    format("Handler for categories {0} and {1} are already registered", categoryA, categoryB));
            }
        }

        collisionHandlers.add(new CollisionHandlerEntry(categoryA, categoryB, collisionHandler));
    }

    public void update(final Duration elapsedTime) {
        // Variable time step is not recommended, but we'll be careful... We can change this later if it causes
        // trouble, but otherwise, it would be nice to have 1:1 entity::update and physics::update steps.
        world.step(elapsedTime.getSeconds(), VELOCITY_ITERATIONS, POSITION_ITERATIONS);

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
}
