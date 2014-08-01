package dhcoder.support.utils;

import dhcoder.support.collision.shape.Circle;
import dhcoder.support.collision.shape.Rectangle;
import dhcoder.support.collision.shape.Shape;

import static dhcoder.support.utils.MathUtils.clamp;
import static dhcoder.support.utils.StringUtils.format;

public final class ShapeUtils {

    public static boolean testIntersection(final Shape shape1, final float x1, final float y1, final Shape shape2,
        final float x2, final float y2) {
        // This code here is ugly - using a map<pair, function> would be better. But this way avoids needing to
        // allocate a pair each time just to test types.
        if (shape1 instanceof Circle && shape2 instanceof Circle) {
            return testCircleIntersection((Circle)shape1, x1, y1, (Circle)shape2, x2, y2);
        }
        else if (shape1 instanceof Rectangle && shape2 instanceof Rectangle) {
            return testRectangleIntersection((Rectangle)shape1, x1, y1, (Rectangle)shape2, x2, y2);
        }
        else if (shape1 instanceof Circle && shape2 instanceof Rectangle) {
            return testCircleRectangleIntersection((Circle)shape1, x1, y1, (Rectangle)shape2, x2, y2);
        }
        else if (shape1 instanceof Rectangle && shape2 instanceof Circle) {
            return testCircleRectangleIntersection((Circle)shape2, x1, y1, (Rectangle)shape1, x2, y2);
        }

        throw new IllegalArgumentException(
            format("Unexpected shapes passed in for intersection testing: {0} & {1}", shape1.getClass(),
                shape2.getClass()));
    }

    private static boolean testCircleIntersection(final Circle circle1, final float x1, final float y1,
        final Circle circle2, final float x2, final float y2) {
        // Two circles intersect if the sum of their radii is less than the distance between their two centers
        float deltaX = x2 - x1;
        float deltaY = y2 - y1;
        float radiiSum = circle1.getRadius() + circle2.getRadius();
        float dist2 = deltaX * deltaX + deltaY * deltaY;

        return dist2 < (radiiSum * radiiSum);
    }

    private static boolean testRectangleIntersection(final Rectangle rect1, final float x1, final float y1,
        final Rectangle rect2, final float x2, final float y2) {
        // Two rectangles overlap if their corners are bounded by each other.
        // See also: http://stackoverflow.com/a/306332/1299302
        return (rect1.getLeft(x1) < rect2.getRight(x2) && rect2.getLeft(x2) < rect1.getRight(x1) &&
            rect1.getBottom(y1) < rect2.getTop(y2) && rect2.getBottom(y2) < rect1.getTop(y1));
    }

    private static boolean testCircleRectangleIntersection(final Circle circle, final float x1, final float y1,
        final Rectangle rect, final float x2, final float y2) {
        // This algorithm finds the point within the rectangle closest to the origin of the circle and compares it to
        // to origin of the circle. See also: http://stackoverflow.com/a/1879223/1299302
        float closestX = clamp(x1, rect.getLeft(x2), rect.getRight(x2));
        float closestY = clamp(y1, rect.getBottom(y2), rect.getTop(y2));

        float deltaX = x1 - closestX;
        float deltaY = y1 - closestY;
        float radius = circle.getRadius();

        return (deltaX * deltaX + deltaY * deltaY) < (radius * radius);
    }

    private ShapeUtils() {} // Disabled constructor

}
