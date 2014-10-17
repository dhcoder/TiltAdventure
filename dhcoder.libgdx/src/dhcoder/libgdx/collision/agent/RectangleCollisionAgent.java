package dhcoder.libgdx.collision.agent;

import com.badlogic.gdx.math.Vector2;
import dhcoder.libgdx.collision.shape.Rectangle;
import dhcoder.libgdx.collision.shape.Shape;

/**
 * Class that provides various collision operations between two rectangles.
 */
public final class RectangleCollisionAgent implements CollisionAgent {

    @Override
    public boolean testIntersection(final Shape shape1, final float x1, final float y1, final Shape shape2,
        final float x2, final float y2) {

        Rectangle rect1 = (Rectangle)shape1;
        Rectangle rect2 = (Rectangle)shape2;

        // Two rectangles overlap if their corners are bounded by each other.
        // See also: http://stackoverflow.com/a/306332/1299302
        return (rect1.getLeft(x1) < rect2.getRight(x2) && rect2.getLeft(x2) < rect1.getRight(x1) &&
            rect1.getBottom(y1) < rect2.getTop(y2) && rect2.getBottom(y2) < rect1.getTop(y1));
    }

    @Override
    public void getNormal(final Shape shape1, final float x1, final float y1, final Shape shape2, final float x2,
        final float y2, final Vector2 outNormal) {
        Rectangle rect1 = (Rectangle)shape1;
        Rectangle rect2 = (Rectangle)shape2;

        if (rect1.getRight(x1) < rect2.getLeft(x2)) {
            outNormal.set(-1, 0);
        }
        else if (rect1.getLeft(x1) > rect2.getRight(x2)) {
            outNormal.set(1, 0);
        }
        else if (rect1.getTop(y1) < rect2.getBottom(y2)) {
            outNormal.set(0, -1);
        }
        else if (rect1.getBottom(y1) > rect2.getTop(y2)) {
            outNormal.set(0, 1);
        }
    }
}
