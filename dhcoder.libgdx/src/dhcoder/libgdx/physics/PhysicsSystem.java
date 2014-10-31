package dhcoder.libgdx.physics;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Box2D;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import dhcoder.support.opt.Opt;
import dhcoder.support.time.Duration;

/**
 * Constants for our physics system
 */
public final class PhysicsSystem {
    public interface PostUpdateMethod {
        public void postUpdate(Array<Body> bodies);
    }

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
    private final Opt<PostUpdateMethod> postUpdateMethodOpt = Opt.withNoValue();
    private Box2DDebugRenderer collisionRenderer;
    private Array<Body> bodies;

    public PhysicsSystem() {
        this.world = new World(new Vector2(0f, 0f), true);
        world.setContactListener(new CollisionListener());
    }

    public void setPostUpdateMethod(final PostUpdateMethod postUpdateMethod) {
        postUpdateMethodOpt.set(postUpdateMethod);
    }

    public World getWorld() {
        return world;
    }

    public void update(final Duration elapsedTime) {
        // Variable time step is not recommended, but we'll be careful... We can change this later if it causes
        // trouble, but otherwise, it would be nice to have 1:1 entity::update and physics::update steps.
        world.step(elapsedTime.getSeconds(), VELOCITY_ITERATIONS, POSITION_ITERATIONS);

        if (postUpdateMethodOpt.hasValue()) {
            if (bodies == null) {
                bodies = new Array<Body>(world.getBodyCount());
            }

            world.getBodies(bodies);
            postUpdateMethodOpt.getValue().postUpdate(bodies);
        }
    }

    public void debugRender(final Matrix4 cameraMatrix) {
        if (collisionRenderer == null) {
            collisionRenderer = new Box2DDebugRenderer();
        }

        collisionRenderer.render(world, cameraMatrix);
    }

    public void dispose() {
        if (collisionRenderer != null) {
            collisionRenderer.dispose();
        }
        world.dispose();
    }
}
