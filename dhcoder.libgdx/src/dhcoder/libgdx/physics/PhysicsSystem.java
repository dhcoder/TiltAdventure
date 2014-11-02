package dhcoder.libgdx.physics;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2D;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import dhcoder.support.time.Duration;

/**
 * Constants for our physics system
 */
public final class PhysicsSystem {
    private static final class CollisionListener implements ContactListener {
        @Override
        public void beginContact(final Contact contact) {

        }

        @Override
        public void endContact(final Contact contact) {

        }

        @Override
        public void preSolve(final Contact contact, final Manifold oldManifold) {

        }

        @Override
        public void postSolve(final Contact contact, final ContactImpulse impulse) {

        }
    }

    static {
        Box2D.init();
    }

    // Recommended values from Box2D manual
    private static final int VELOCITY_ITERATIONS = 6;
    private static final int POSITION_ITERATIONS = 2;
    private final World world;
    private final Array<PhysicsElement> physicsElements;
    private Box2DDebugRenderer collisionRenderer;
    private Matrix4 debugRenderMatrix;

    public PhysicsSystem(final int capacity) {
        this.world = new World(new Vector2(0f, 0f), true);
        world.setContactListener(new CollisionListener());

        physicsElements = new Array<PhysicsElement>(false, capacity);
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
