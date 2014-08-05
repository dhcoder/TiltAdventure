package dhcoder.support.collision.agent;

import dhcoder.support.collision.Intersection;
import dhcoder.support.collision.shape.Rectangle;
import dhcoder.support.collision.shape.Shape;
import dhcoder.support.math.Vec2;
import dhcoder.support.memory.Pool;
import dhcoder.support.opt.OptFloat;

/**
 * Interface that provides various collision operations between two shapes.
 */
public final class RectangleCollisionAgent implements CollisionAgent {

    Pool<OptFloat> optFloatPool = Pool.of(OptFloat.class, 4);
    Pool<Vec2> vecPool = Pool.of(Vec2.class, 3);

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
    public void getIntersection(final Shape shape1, final float fromX1, final float fromY1, final float toX1,
        final float toY1, final Shape shape2, final float fromX2, final float fromY2, final float toX2,
        final float toY2, final Intersection outIntersection) {

        Rectangle rect1 = (Rectangle)shape1;
        Rectangle rect2 = (Rectangle)shape2;

        OptFloat timeCollidedLeftOpt = optFloatPool.grabNew();
        OptFloat timeCollidedRightOpt = optFloatPool.grabNew();
        OptFloat timeCollidedTopOpt = optFloatPool.grabNew();
        OptFloat timeCollidedBottomOpt = optFloatPool.grabNew();

        // We do this test from rect2's frame of reference, as if he were located at the origin, not moving, the whole
        // time.
        Vec2 velRect1 = vecPool.grabNew();
        Vec2 velRect2 = vecPool.grabNew();
        Vec2 relVelRect1 = vecPool.grabNew();

        velRect1.set(toX1 - fromX1, toY1 - fromY1);
        velRect2.set(toX2 - fromX2, toY2 - fromY2);
        relVelRect1.set(velRect1);
        relVelRect1.add(velRect1);

        vecPool.free(velRect1);
        vecPool.free(velRect2);

        float startX = fromX1 - fromX2;
        float startY = fromY1 - fromY2;
        float endX = startX + relVelRect1.getX();
        float endY = startY + relVelRect1.getY();

        vecPool.free(relVelRect1);

        float rect1StartLeft = rect1.getLeft(startX);
        float rect1StartRight = rect1.getRight(startX);
        float rect1StartTop = rect1.getTop(startY);
        float rect1StartBottom = rect1.getBottom(startY);
        float rect1EndLeft = rect1.getLeft(endX);
        float rect1EndRight = rect1.getRight(endX);
        float rect1EndTop = rect1.getTop(endY);
        float rect1EndBottom = rect1.getBottom(endY);

        float rect2left = rect2.getLeft(0f);
        float rect2right = rect2.getRight(0f);
        float rect2top = rect2.getTop(0f);
        float rect2bottom = rect2.getBottom(0f);

        if (rect1StartRight < rect2left && rect1EndRight >= rect2left) {
            timeCollidedLeftOpt.set((rect2left - rect1StartRight) / (rect1EndRight - rect1StartRight));
        }
        else if (rect1StartLeft > rect2right && rect1EndLeft <= rect2right) {
            timeCollidedRightOpt.set((rect1StartLeft - rect2right) / (rect1StartLeft - rect1EndLeft));
        }

        if (rect1StartTop < rect2bottom && rect1EndTop >= rect2bottom) {
            timeCollidedBottomOpt.set((rect2bottom - rect1StartTop) / (rect1EndTop - rect1StartTop));
        }
        else if (rect1StartBottom > rect2top && rect1EndBottom <= rect2top) {
            timeCollidedTopOpt.set((rect1StartBottom - rect2top) / (rect1StartBottom - rect1EndBottom));
        }

        float percent = Math.min(timeCollidedLeftOpt.getValueOr(1f), Math.min(timeCollidedRightOpt.getValueOr(1f),
                Math.min(timeCollidedTopOpt.getValueOr(1f), timeCollidedBottomOpt.getValueOr(1f))));

        optFloatPool.free(timeCollidedLeftOpt);
        optFloatPool.free(timeCollidedRightOpt);
        optFloatPool.free(timeCollidedTopOpt);
        optFloatPool.free(timeCollidedBottomOpt);

        float outSourceX = fromX1 + percent * (toX1 - fromX1);
        float outSourceY = fromY1 + percent * (toY1 - fromY1);
        float outTargetX = fromX2 + percent * (toX2 - fromX2);
        float outTargetY = fromY2 + percent * (toY2 - fromY2);

        outIntersection.set(outSourceX, outSourceY, outTargetX, outTargetY, 0f, 0f);
    }
}
