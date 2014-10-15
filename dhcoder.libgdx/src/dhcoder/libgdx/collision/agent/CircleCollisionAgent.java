package dhcoder.libgdx.collision.agent;

import com.badlogic.gdx.math.Vector2;
import dhcoder.libgdx.collision.shape.Circle;
import dhcoder.libgdx.collision.shape.Shape;
import dhcoder.libgdx.pool.Vector2PoolBuilder;
import dhcoder.support.memory.Pool;

/**
 * Class that provides various collision operations between two circles.
 */
public final class CircleCollisionAgent implements CollisionAgent {

    Pool<Vector2> vectorPool = Vector2PoolBuilder.build(1);

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
    public void getRepulsion(final Shape shape1, final float fromX1, final float fromY1, final float toX1,
        final float toY1, final Shape shape2, final float fromX2, final float fromY2, final float toX2,
        final float toY2, final Vector2 outRepulsion) {

        Circle circle1 = (Circle)shape1;
        Circle circle2 = (Circle)shape2;

        Vector2 circleDistance = vectorPool.grabNew();
        circleDistance.set(toX1 - toX2, toY1 - toY2); // Vector points from circle2 to circle1

        float penetration = circle1.getRadius() + circle2.getRadius() - circleDistance.len();

        // The repulsion vector points from circle2 to circle1, with enough force to push out the penetration that
        // occurred.
        outRepulsion.set(circleDistance).nor().scl(penetration);

        vectorPool.free(circleDistance);
    }

    @Override
    public void getNormal(final Shape shape1, final float x1, final float y1, final Shape shape2, final float x2,
        final float y2, final Vector2 outNormal) {
        outNormal.set(x2, y2).sub(x1, y1).nor();
    }
}
