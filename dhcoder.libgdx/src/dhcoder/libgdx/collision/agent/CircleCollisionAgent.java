package dhcoder.libgdx.collision.agent;

import com.badlogic.gdx.math.Vector2;
import dhcoder.libgdx.collision.shape.Circle;
import dhcoder.libgdx.collision.shape.Shape;

/**
 * Class that provides various collision operations between two circles.
 */
public final class CircleCollisionAgent implements CollisionAgent {

    @Override
    public boolean testIntersection(final Shape shape1, final float x1, final float y1, final Shape shape2,
        final float x2, final float y2) {

        Circle circle1 = (Circle)shape1;
        Circle circle2 = (Circle)shape2;

        // Two circles intersect if the sum of their radii is less than the distance between their two centers
        float deltaX = x2 - x1;
        float deltaY = y2 - y1;
        float radiiSum = circle1.getRadius() + circle2.getRadius();
        float dist2 = deltaX * deltaX + deltaY * deltaY;

        return dist2 < (radiiSum * radiiSum);

    }

    @Override
    public void getNormal(final Shape shape1, final float x1, final float y1, final Shape shape2, final float x2,
        final float y2, final Vector2 outNormal) {
        outNormal.set(x1, y1).sub(x2, y2).nor();
    }
}
