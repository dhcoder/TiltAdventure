package dhcoder.support.collision;

import dhcoder.support.collision.shape.Shape;
import dhcoder.support.event.Event;

/**
 * A handle to information in {@link CollisionManager}'s system, which you can use to update the information of a
 * shape registered with it.
 */
public final class CollisionHandle {

    public final Event onCollision = new Event();

    private Shape shape;
    private int groupIndex;

    public Shape getShape() {
        return shape;
    }

    void setShape(final Shape shape) {
        this.shape = shape;
    }

    public void updateOrigin(final float x, final float y) {
        shape.setOrigin(x, y);
    }

    int getGroupIndex() {
        return groupIndex;
    }

    void setGroupIndex(final int groupIndex) {
        this.groupIndex = groupIndex;
    }

    void reset() {
        shape = null;
        groupIndex = -1;
        onCollision.clearListeners();
    }

    void fireCollision(final CollisionHandle otherHandle) {
        onCollision.fire(this);
    }
}
