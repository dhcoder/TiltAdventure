package dhcoder.support.collision;

import dhcoder.support.collision.shape.Shape;
import dhcoder.support.event.Event;

import static dhcoder.support.utils.ShapeUtils.testIntersection;

/**
 * A simplified representation of some game object used in collision testing.
 */
public final class Collider {

    public final Event onCollision = new Event();
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
    void reset() {
        shape = null;
        groupIndex = -1;
        onCollision.clearListeners();
        isActive = false;
    }

    // Should only be called by CollisionSystem
    void testCollisionWith(final Collider otherCollider) {
        if (!isActive) {
            return;
        }

        if (otherCollider == this) {
            return;
        }

        if (testIntersection(shape, currX, currY, otherCollider.shape, otherCollider.currX, otherCollider.currY)) {
            onCollision.fire(this);
        }
    }

}
