package dhcoder.libgdx.collision.agent;

import com.badlogic.gdx.math.Vector2;
import dhcoder.libgdx.collision.shape.Circle;
import dhcoder.libgdx.collision.shape.Rectangle;
import dhcoder.libgdx.collision.shape.Shape;
import dhcoder.libgdx.pool.VectorPoolBuilder;
import dhcoder.support.memory.Pool;
import dhcoder.support.opt.OptFloat;

import static dhcoder.support.math.MathUtils.clamp;

/**
 * Interface that provides various collision operations between two shapes.
 */
public final class CircleRectangleCollisionAgent implements CollisionAgent {

    Pool<Vector2> vectorPool = VectorPoolBuilder.build(1);

    public static CollisionAgent reverse() {
        return new ReverseCollisionAgent(new CircleRectangleCollisionAgent());
    }

    @Override
    public boolean testIntersection(final Shape shape1, final float x1, final float y1, final Shape shape2,
        final float x2, final float y2) {

        Circle circle = (Circle)shape1;
        Rectangle rect = (Rectangle)shape2;

        // This algorithm finds the point within the rectangle closest to the origin of the circle and compares it to
        // to origin of the circle. See also: http://stackoverflow.com/a/1879223/1299302
        float closestX = clamp(x1, rect.getLeft(x2), rect.getRight(x2));
        float closestY = clamp(y1, rect.getBottom(y2), rect.getTop(y2));

        float deltaX = x1 - closestX;
        float deltaY = y1 - closestY;
        float radius = circle.getRadius();

        return (deltaX * deltaX + deltaY * deltaY) < (radius * radius);
    }

    @Override
    public void getRepulsion(final Shape shape1, final float fromX1, final float fromY1, final float toX1,
        final float toY1, final Shape shape2, final float fromX2, final float fromY2, final float toX2,
        final float toY2, final Vector2 outRepulsion) {

        Circle circle = (Circle)shape1;
        Rectangle rect = (Rectangle)shape2;

        float closestX = clamp(toX1, rect.getLeft(toX2), rect.getRight(toX2));
        float closestY = clamp(toY1, rect.getBottom(toY2), rect.getTop(toY2));

        Vector2 circleToPointDistance = vectorPool.grabNew();
        circleToPointDistance.set(toX1 - closestX, toY1 - closestY); // Vector points from closest point to circle1

        float penetration = circle.getRadius() - circleToPointDistance.len();
        outRepulsion.set(circleToPointDistance).nor().scl(penetration);

        vectorPool.free(circleToPointDistance);
    }
}

