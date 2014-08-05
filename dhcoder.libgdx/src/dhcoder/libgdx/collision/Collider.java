package dhcoder.libgdx.collision;

import dhcoder.libgdx.collision.shape.Shape;
import dhcoder.support.event.ArgEvent;
import dhcoder.support.memory.Pool;
import dhcoder.support.memory.Poolable;

import static dhcoder.libgdx.collision.shape.ShapeUtils.testIntersection;

/**
 * A simplified representation of some game object used in collision testing.
 */
public final class Collider implements Poolable {

    private static final Pool<CollisionEventArgs> collisionEventArgsPool = Pool.of(CollisionEventArgs.class, 1);

    public final ArgEvent<CollisionEventArgs> onCollided = new ArgEvent<CollisionEventArgs>();
    public final ArgEvent<CollisionEventArgs> onSeparated = new ArgEvent<CollisionEventArgs>();

    private boolean isActive;
    private float lastX, lastY;
    private float currX, currY;
    private Shape shape;
    private int groupId;

    public boolean isActive() {
        return isActive;
    }

    public Shape getShape() {
        return shape;
    }

    public int getGroupId() {
        return groupId;
    }

    public float getLastX() {
        return lastX;
    }

    public float getLastY() {
        return lastY;
    }

    public float getCurrX() {
        return currX;
    }

    public float getCurrY() {
        return currY;
    }

    public void updatePosition(final float x, final float y) {
        lastX = currX;
        lastY = currY;
        currX = x;
        currY = y;

        if (!isActive) {
            isActive = true;
            lastX = x;
            lastY = y;
        }

    }

    // Should only be called by CollisionSystem

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

        return testIntersection(shape, currX, currY, otherCollider.shape, otherCollider.currX, otherCollider.currY);
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

    // Should only be called by Collision
    // Used to rewrite history, telling the collider that it didn't really go to where it thought it did...
    // This is useful, for example, to pop a shape out after it penetrated an object that should be solid.
    void fixCurrentPosition(final float currX, final float currY) {
        this.currX = currX;
        this.currY = currY;
    }

}

