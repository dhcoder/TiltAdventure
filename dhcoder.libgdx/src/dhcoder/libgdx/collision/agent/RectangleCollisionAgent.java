package dhcoder.libgdx.collision.agent;

import com.badlogic.gdx.math.Vector2;
import dhcoder.libgdx.collision.Intersection;
import dhcoder.libgdx.collision.shape.Rectangle;
import dhcoder.libgdx.collision.shape.Shape;
import dhcoder.libgdx.pool.VectorPoolBuilder;
import dhcoder.support.memory.Pool;
import dhcoder.support.opt.OptFloat;

/**
 * Interface that provides various collision operations between two shapes.
 */
public final class RectangleCollisionAgent implements CollisionAgent {

    Pool<OptFloat> optFloatPool = Pool.of(OptFloat.class, 1);
    Pool<Vector2> vectorPool = VectorPoolBuilder.build(3);

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

        // We do this test from rect2's frame of reference, as if he were located at the origin, not moving, the whole
        // time.
        Vector2 velRect1 = vectorPool.grabNew();
        Vector2 velRect2 = vectorPool.grabNew();
        Vector2 relVelRect1 = vectorPool.grabNew();

        velRect1.set(toX1 - fromX1, toY1 - fromY1);
        velRect2.set(toX2 - fromX2, toY2 - fromY2);
        relVelRect1.set(velRect1);
        relVelRect1.add(velRect1);

        vectorPool.free(velRect1);
        vectorPool.free(velRect2);

        float startX = fromX1 - fromX2;
        float startY = fromY1 - fromY2;
        float endX = startX + relVelRect1.x;
        float endY = startY + relVelRect1.y;

        vectorPool.free(relVelRect1);

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

        OptFloat percentWhenCollidedOpt = optFloatPool.grabNew();
        Vector2 normal = vectorPool.grabNew();
        if (rect1StartRight <= rect2left && rect1EndRight >= rect2left) {
            float percentCollidedLeft = (rect2left - rect1StartRight) / (rect1EndRight - rect1StartRight);
            percentWhenCollidedOpt.set(percentCollidedLeft);
            normal.set(-1f, 0f);
        }
        else if (rect1StartLeft >= rect2right && rect1EndLeft <= rect2right) {
            float percentCollidedRight = (rect1StartLeft - rect2right) / (rect1StartLeft - rect1EndLeft);
            percentWhenCollidedOpt.set(percentCollidedRight);
            normal.set(1f, 0f);
        }

        if (rect1StartTop <= rect2bottom && rect1EndTop >= rect2bottom) {
            float percentCollidedBottom = (rect2bottom - rect1StartTop) / (rect1EndTop - rect1StartTop);
            if (percentWhenCollidedOpt.getValueOr(1f) > percentCollidedBottom) {
                percentWhenCollidedOpt.set(percentCollidedBottom);
                normal.set(0f, -1f);
            }
        }
        else if (rect1StartBottom >= rect2top && rect1EndBottom <= rect2top) {
            float percentCollidedTop = (rect1StartBottom - rect2top) / (rect1StartBottom - rect1EndBottom);
            if (percentWhenCollidedOpt.getValueOr(1f) > percentCollidedTop) {
                percentWhenCollidedOpt.set(percentCollidedTop);
                normal.set(0f, 1f);
            }
        }

        float percentWhenCollided = percentWhenCollidedOpt.getValueOr(0f);
        optFloatPool.free(percentWhenCollidedOpt);

        float outSourceX = fromX1 + percentWhenCollided * (toX1 - fromX1);
        float outSourceY = fromY1 + percentWhenCollided * (toY1 - fromY1);
        float outTargetX = fromX2 + percentWhenCollided * (toX2 - fromX2);
        float outTargetY = fromY2 + percentWhenCollided * (toY2 - fromY2);

        outIntersection.set(outSourceX, outSourceY, outTargetX, outTargetY, normal.x, normal.y);
        vectorPool.free(normal);
    }
}
