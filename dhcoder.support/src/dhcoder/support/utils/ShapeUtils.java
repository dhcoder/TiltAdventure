package dhcoder.support.utils;

import dhcoder.support.collision.shape.Circle;
import dhcoder.support.collision.shape.Rectangle;
import dhcoder.support.collision.shape.Shape;

import static dhcoder.support.utils.StringUtils.format;

public final class ShapeUtils {

    public static boolean testIntersection(final Shape shape1, final Shape shape2) {
        // This code here is ugly - using a map<pair, function> would be better. But this way avoids needing to
        // allocate a pair each time just to test types.
        if (shape1 instanceof Circle && shape2 instanceof Circle) {
            return testCircleIntersection((Circle)shape1, (Circle)shape2);
        }
        else {
            return testDefaultIntersection(shape1, shape2);
        }
    }

    private static boolean testCircleIntersection(final Circle circle1, final Circle circle2) {
        // Two circles intersect if the sum of their radii is less than the distance between their two centers
        float deltaX = circle2.getX() - circle1.getX();
        float deltaY = circle2.getY() - circle1.getY();
        float radiiSum = circle1.getRadius() + circle2.getRadius();
        float dist2 = deltaX * deltaX + deltaY * deltaY;

        return dist2 <= (radiiSum * radiiSum);
    }

    // Fallback intersection is good enough for rect-to-rect and circle-to-rect intersection (this shortcut will break
    // and have to be revisited if we ever introduce rotations into our collision system)
    private static boolean testDefaultIntersection(final Shape shape1, final Shape shape2) {
        float shape1X0 = shape1.getX0();
        float shape1Y0 = shape1.getY0();
        float shape1X1 = shape1.getX1();
        float shape1Y1 = shape1.getY1();
        float shape2X0 = shape2.getX0();
        float shape2Y0 = shape2.getY0();
        float shape2X1 = shape2.getX1();
        float shape2Y1 = shape2.getY1();

        return shape1.containsPoint(shape2X0, shape2Y0) ||
            shape1.containsPoint(shape2X0, shape2Y1) ||
            shape1.containsPoint(shape2X1, shape2Y0) ||
            shape1.containsPoint(shape2X1, shape2Y1) ||
            shape2.containsPoint(shape1X0, shape1Y0) ||
            shape2.containsPoint(shape1X0, shape1Y1) ||
            shape2.containsPoint(shape1X1, shape1Y0) ||
            shape2.containsPoint(shape1X1, shape1Y1);
    }

    private ShapeUtils() {} // Disabled constructor

}
