package dhcoder.libgdx.collision;

import com.badlogic.gdx.math.Vector2;
import dhcoder.libgdx.collision.shape.Shape;
import dhcoder.support.event.ArgEvent;
import dhcoder.support.memory.Pool;
import dhcoder.support.memory.Poolable;

import static dhcoder.libgdx.collision.shape.ShapeUtils.testIntersection;

/**
 * A class that encapsulates a shape and position used for collision testing.
 */
public final class Collider implements Poolable {

    private static final Pool<CollisionEventArgs> collisionEventArgsPool = Pool.of(CollisionEventArgs.class, 1);

    public final ArgEvent<CollisionEventArgs> onCollided = new ArgEvent<CollisionEventArgs>();
    public final ArgEvent<CollisionEventArgs> onSeparated = new ArgEvent<CollisionEventArgs>();
    private final Vector2 lastPosition = new Vector2();
    private final Vector2 currPosition = new Vector2();

    private boolean isActive;
    private Shape shape;
    private int groupId;

    public boolean isActive() {
        return isActive;
    }

    public Shape getShape() {
        return shape;
    }

    /**
     * Returns the ID of the group this collider belongs in.
     */
    public int getGroupId() {
        return groupId;
    }

    public Vector2 getCurrPosition() {
        return currPosition;
    }

    // Should only be called by Collision
    // Used to rewrite history, telling the collider that it didn't really go to where it thought it did...
    // This is useful, for example, to pop a shape out after it penetrated an object that should be solid.
    void setCurrPosition(final Vector2 currPosition) {
        this.currPosition.set(currPosition);
    }

    public Vector2 getLastPosition() {
        return lastPosition;
    }

    public void updatePosition(final float x, final float y) {
        lastPosition.set(currPosition);
        currPosition.set(x, y);

        if (!isActive) {
            isActive = true; // If just activated, currPosition wasn't valid until just now
            lastPosition.set(currPosition);
        }
    }

    // Should only be called by CollisionSystem
    @Override
    public void reset() {
        shape = null;
        groupId = -1;
        onCollided.clearListeners();
        isActive = false;
    }

    /**
     * Initialize this collider with its shape. However, the collider won't be active until you call {@link
     * #updatePosition(float, float)} for the first time.
     */
    // Should only be called by CollisionSystem
    void initialize(final int groupId, final Shape shape) {
        this.groupId = groupId;
        this.shape = shape;
        isActive = false;
    }

    // Should only be called by CollisionSystem
    boolean collidesWith(final Collider otherCollider) {
        if (!isActive || !otherCollider.isActive || otherCollider == this) {
            return false;
        }

        return testIntersection(shape, currPosition.x, currPosition.y, otherCollider.shape,
            otherCollider.currPosition.x, otherCollider.currPosition.y);
    }

    // Should only be called by CollisionSystem
    void fireCollision(final Collision collision) {
        if (onCollided.hasListeners()) {
            CollisionEventArgs args = collisionEventArgsPool.grabNew();
            args.setCollision(collision);
            onCollided.fire(this, args);
            collisionEventArgsPool.free(args);
        }
    }

    // Should only be called by CollisionSystem
    void fireSeparation(final Collision collision) {
        if (onSeparated.hasListeners()) {
            CollisionEventArgs args = collisionEventArgsPool.grabNew();
            args.setCollision(collision);
            onSeparated.fire(this, args);
            collisionEventArgsPool.free(args);
        }
    }

}

