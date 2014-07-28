package dhcoder.support.utils;

import dhcoder.support.collision.shape.Circle;
import dhcoder.support.collision.shape.Rectangle;
import dhcoder.support.collision.shape.Shape;

import static dhcoder.support.utils.MathUtils.clamp;
import static dhcoder.support.utils.StringUtils.format;

public final class ShapeUtils {

    public static boolean testIntersection(final Shape shape1, final Shape shape2) {
        // This code here is ugly - using a map<pair, function> would be better. But this way avoids needing to
        // allocate a pair each time just to test types.
        if (shape1 instanceof Circle && shape2 instanceof Circle) {
            return testCircleIntersection((Circle)shape1, (Circle)shape2);
        }
        else if (shape1 instanceof Rectangle && shape2 instanceof Rectangle) {
            return testRectangleIntersection((Rectangle)shape1, (Rectangle)shape2);
        }
        else if (shape1 instanceof Circle && shape2 instanceof Rectangle) {
            return testCircleRectangleIntersection((Circle)shape1, (Rectangle)shape2);
        }
        else if (shape1 instanceof Rectangle && shape2 instanceof Circle) {
            return testCircleRectangleIntersection((Circle)shape2, (Rectangle)shape1);
        }

        throw new IllegalArgumentException(
            format("Unexpected shapes passed in for intersection testing: {0} & {1}", shape1.getClass(),
                shape2.getClass()));
    }

    private static boolean testCircleIntersection(final Circle circle1, final Circle circle2) {
        // Two circles intersect if the sum of their radii is less than the distance between their two centers
        float deltaX = circle2.getX() - circle1.getX();
        float deltaY = circle2.getY() - circle1.getY();
        float radiiSum = circle1.getRadius() + circle2.getRadius();
        float dist2 = deltaX * deltaX + deltaY * deltaY;

        return dist2 <= (radiiSum * radiiSum);
    }

    private static boolean testRectangleIntersection(final Rectangle rect1, final Rectangle rect2) {
        // Two rectangles overlap if their corners are bounded by each other.
        // See also: http://stackoverflow.com/a/306332/1299302
        return (rect1.getX0() <= rect2.getX1() && rect2.getX0() <= rect1.getX1() &&
            rect1.getY0() <= rect2.getY1() && rect2.getY0() <= rect1.getY1());
    }

    private static boolean testCircleRectangleIntersection(final Circle circle, final Rectangle rect) {
        // This algorithm finds the point within the rectangle closest to the origin of the circle and compares it to
        // to origin of the circle. See also: http://stackoverflow.com/a/1879223/1299302
        float closestX = clamp(circle.getX(), rect.getX0(), rect.getX1());
        float closestY = clamp(circle.getY(), rect.getY0(), rect.getY1());

        float deltaX = circle.getX() - closestX;
        float deltaY = circle.getY() - closestY;
        float radius = circle.getRadius();

        return (deltaX * deltaX + deltaY * deltaY) <= (radius * radius);
    }

    private ShapeUtils() {} // Disabled constructor

}
