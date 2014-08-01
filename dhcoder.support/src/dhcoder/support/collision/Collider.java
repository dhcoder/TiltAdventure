package dhcoder.support.collision;

import dhcoder.support.collision.shape.Shape;
import dhcoder.support.event.ArgEvent;
import dhcoder.support.event.Event;
import dhcoder.support.event.EventArgs;
import dhcoder.support.memory.Pool;
import dhcoder.support.memory.Poolable;

import static dhcoder.support.utils.ShapeUtils.testIntersection;

/**
 * A simplified representation of some game object used in collision testing.
 */
public final class Collider implements Poolable {

    private static Pool<CollisionEventArgs> collisionEventArgsPool = Pool.of(CollisionEventArgs.class, 1);

    public final ArgEvent<CollisionEventArgs> onCollision = new ArgEvent<CollisionEventArgs>();
    private boolean isActive;
    private float lastX, lastY;
    private float currX, currY;
    private Shape shape;
    private int groupIndex;

    public boolean isActive() {
        return isActive;
    }

    public Shape getShape() {
        return shape;
    }

    public int getGroupIndex() {
        return groupIndex;
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
        groupIndex = -1;
        onCollision.clearListeners();
        isActive = false;
    }

    /**
     * Initialize this collider with its shape. However, the collider won't be active until you call {@link
     * #updatePosition(float, float)} for the first time.
     */
    void initialize(final int groupIndex, final Shape shape) {
        this.groupIndex = groupIndex;
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

    void fireCollision(final Collision collision) {
        if (onCollision.hasListeners()) {
            CollisionEventArgs args = collisionEventArgsPool.grabNew();
            args.setCollision(collision);
            onCollision.fire(this, args);
            collisionEventArgsPool.free(args);
        }
    }
}

