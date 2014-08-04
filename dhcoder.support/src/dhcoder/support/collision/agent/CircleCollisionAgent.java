package dhcoder.support.collision.agent;

import dhcoder.support.collision.Intersection;
import dhcoder.support.collision.shape.Circle;
import dhcoder.support.collision.shape.Shape;
import dhcoder.support.math.Vec2;
import dhcoder.support.memory.Pool;
import dhcoder.support.opt.OptFloat;

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

        // Treat the distance moved by the circles as a velocity. Then, get the velocity relative to the first circle
        // as if it were standing still. We'll use this later to figure out how much time it took for the two circles
        // to penetrate each other.
        Vec2 velCircle1 = vecPool.grabNew();
        Vec2 velCircle2 = vecPool.grabNew();
        Vec2 velRelCircle2 = vecPool.grabNew(); // Circle2's speed relative to Circle1
        velCircle1.set(toX1 - fromX1, toY1 - fromY1);
        velCircle2.set(toX2 - fromX2, toY2 - fromY2);
        velRelCircle2.set(velCircle2);
        velRelCircle2.sub(velCircle1);

        // Calculate the final distance between the two circles compared to the final distance between the radii
        // (In other words, the amount of penetration)
        Vec2 finalDist = vecPool.grabNew();
        finalDist.set(toX2 - toX1, toY2 - toY1);

        float rSum = circle1.getRadius() + circle2.getRadius();
        float penetration = rSum - finalDist.len();

        // Finally, calculate what percentage of circle2's travelling time was spent penetrating circle1.

        float penetrationPercentage = penetration / velRelCircle2.len();
        penetrationPercentage = clamp(penetrationPercentage, 0f, 1f);
        float notPenetratedPercentage = 1f - penetrationPercentage;

        vecPool.free(velCircle1);
        vecPool.free(velCircle2);
        vecPool.free(velRelCircle2);
        vecPool.free(finalDist);

        float outSourceX = fromX1 + notPenetratedPercentage * (toX1 - fromX1);
        float outSourceY = fromY1 + notPenetratedPercentage * (toY1 - fromY1);
        float outTargetX = fromX2 + notPenetratedPercentage * (toX2 - fromX2);
        float outTargetY = fromY2 + notPenetratedPercentage * (toY2 - fromY2);

        Vec2 normalForce = vecPool.grabNew();
        normalForce.set(outSourceX - outTargetX, outSourceY - outTargetY);
        normalForce.toUnit();
        normalForce.mul(penetration, penetration);
        outIntersection.set(outSourceX, outSourceY, outTargetX, outTargetY, normalForce.getX(), normalForce.getY());
        vecPool.free(normalForce);
    }
}
