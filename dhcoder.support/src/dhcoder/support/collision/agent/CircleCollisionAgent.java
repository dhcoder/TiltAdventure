package dhcoder.support.collision.agent;

import dhcoder.support.collision.Intersection;
import dhcoder.support.collision.shape.Circle;
import dhcoder.support.collision.shape.Shape;
import dhcoder.support.math.Vec2;
import dhcoder.support.memory.Pool;

import static dhcoder.support.utils.MathUtils.clamp;

/**
 * Interface that provides various collision operations between two shapes.
 */
public final class CircleCollisionAgent implements CollisionAgent {

    Pool<Vec2> vecPool = Pool.of(Vec2.class, 4);

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
    public void getIntersection(final Shape shape1, final float fromX1, final float fromY1, final float toX1,
        final float toY1, final Shape shape2, final float fromX2, final float fromY2, final float toX2,
        final float toY2, final Intersection outIntersection) {

        Circle circle1 = (Circle)shape1;
        Circle circle2 = (Circle)shape2;

        // The algorithm below calculates what percentage of the circle's motion is spent collided and, by extension,
        // not collided. From that, we reset the circles to their start point and move them just the percentage of the
        // way to before collision.

        // Simplify the problem by looking at this problem from circle1's frame of reference, so we only have to worry
        // about the motion of a single body.
        Vec2 velCircle1 = vecPool.grabNew();
        Vec2 velCircle2 = vecPool.grabNew();
        Vec2 velRelCircle2 = vecPool.grabNew();
        velCircle1.set(toX1 - fromX1, toY1 - fromY1);
        velCircle2.set(toX2 - fromX2, toY2 - fromY2);
        velRelCircle2.set(velCircle2);
        velRelCircle2.sub(velCircle1); // Circle2's speed relative to Circle1

        // Figure out how far circle2 penetrated circle1 by comparing the actual distance between the two circles vs.
        // their radii.
        Vec2 circleDistance = vecPool.grabNew();
        circleDistance.set(toX2 - toX1, toY2 - toY1);

        float penetration = circle1.getRadius() + circle2.getRadius() - circleDistance.len();

        // Finally, calculate what percentage of circle2's travelling time was spent after intersecting.
        float collidedPercentage = penetration / velRelCircle2.len();
        collidedPercentage = clamp(collidedPercentage, 0f, 1f); // Clamp needed because of floating point precision
        float separatedPercentage = 1f - collidedPercentage;

        vecPool.free(velCircle1);
        vecPool.free(velCircle2);
        vecPool.free(velRelCircle2);
        vecPool.free(circleDistance);

        float outSourceX = fromX1 + separatedPercentage * (toX1 - fromX1);
        float outSourceY = fromY1 + separatedPercentage * (toY1 - fromY1);
        float outTargetX = fromX2 + separatedPercentage * (toX2 - fromX2);
        float outTargetY = fromY2 + separatedPercentage * (toY2 - fromY2);

        Vec2 normalForce = vecPool.grabNew();
        normalForce.set(outSourceX - outTargetX, outSourceY - outTargetY);
        normalForce.toUnit();
        // Push back with enough force to cancel the penetration that would have happened
        normalForce.scale(penetration);

        outIntersection.set(outSourceX, outSourceY, outTargetX, outTargetY, normalForce.getX(), normalForce.getY());
        vecPool.free(normalForce);
    }
}
