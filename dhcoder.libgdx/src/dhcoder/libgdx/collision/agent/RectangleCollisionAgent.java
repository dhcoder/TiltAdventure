package dhcoder.libgdx.collision.agent;

import com.badlogic.gdx.math.Vector2;
import dhcoder.libgdx.collision.shape.Rectangle;
import dhcoder.libgdx.collision.shape.Shape;
import dhcoder.libgdx.pool.Vector2PoolBuilder;
import dhcoder.support.memory.Pool;
import dhcoder.support.opt.OptFloat;

/**
 * Class that provides various collision operations between two rectangles.
 */
public final class RectangleCollisionAgent implements CollisionAgent {

    Pool<OptFloat> optFloatPool = Pool.of(OptFloat.class, 1);
    Pool<Vector2> vectorPool = Vector2PoolBuilder.build(3);

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
    public void getRepulsion(final Shape shape1, final float fromX1, final float fromY1, final float toX1,
        final float toY1, final Shape shape2, final float fromX2, final float fromY2, final float toX2,
        final float toY2, final Vector2 outRepulsion) {

        Rectangle rect1 = (Rectangle)shape1;
        Rectangle rect2 = (Rectangle)shape2;

        // We do this test from rect2's frame of reference, as if it were located at the origin and not moving. This
        // simplifies a bunch of math, later.
        Vector2 velRect1 = vectorPool.grabNew();
        Vector2 velRect2 = vectorPool.grabNew();
        Vector2 relVelRect1 = vectorPool.grabNew();

        velRect1.set(toX1 - fromX1, toY1 - fromY1);
        velRect2.set(toX2 - fromX2, toY2 - fromY2);
        relVelRect1.set(velRect1);
        relVelRect1.sub(velRect2);

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

        // Now that we're in a new frame of reference (where rect2 stands still and rect1 is moving), calculate the
        // times when rect1 first passes over rect2, as well as the side crossed over. If the shape crossed over two
        // sides at the same time (say, top and left), calculate which side was crossed over first.
        OptFloat percentWhenCollidedOpt = optFloatPool.grabNew();

        // Set to 1.1 instead of 1, to guarantee our repulsion pushes the colliding shape OUT enough. Otherwise, because
        // of float precision, we might only push the colliding shape just too little so it's still inside of us.
        final float REPULSION_SCALAR = 1.1f;
        if (rect1StartRight <= rect2left && rect1EndRight >= rect2left) {
            // Rect1 collided over Rect2 left->right
            float percentCollidedLeft = (rect2left - rect1StartRight) / (rect1EndRight - rect1StartRight);
            percentWhenCollidedOpt.set(percentCollidedLeft);
            outRepulsion.set(-REPULSION_SCALAR, 0f).scl(rect1EndRight - rect2left);
        }
        else if (rect1StartLeft >= rect2right && rect1EndLeft <= rect2right) {
            // Rect1 collided over Rect2 right->left
            float percentCollidedRight = (rect1StartLeft - rect2right) / (rect1StartLeft - rect1EndLeft);
            percentWhenCollidedOpt.set(percentCollidedRight);
            outRepulsion.set(REPULSION_SCALAR, 0f).scl(rect2right - rect1EndLeft);
        }

        if (rect1StartTop <= rect2bottom && rect1EndTop >= rect2bottom) {
            // Rect1 collided over Rect2 bottom->top
            float percentCollidedBottom = (rect2bottom - rect1StartTop) / (rect1EndTop - rect1StartTop);
            if (percentCollidedBottom <= percentWhenCollidedOpt.getValueOr(1f)) {
                percentWhenCollidedOpt.set(percentCollidedBottom);
                outRepulsion.set(0f, -REPULSION_SCALAR).scl(rect1EndTop - rect2bottom);
            }
        }
        else if (rect1StartBottom >= rect2top && rect1EndBottom <= rect2top) {
            // Rect1 collided over Rect2 top->bottom
            float percentCollidedTop = (rect1StartBottom - rect2top) / (rect1StartBottom - rect1EndBottom);
            if (percentCollidedTop <= percentWhenCollidedOpt.getValueOr(1f)) {
                percentWhenCollidedOpt.set(percentCollidedTop);
                outRepulsion.set(0f, REPULSION_SCALAR).scl(rect2top - rect1EndBottom);
            }
        }

        optFloatPool.free(percentWhenCollidedOpt);
    }
}
