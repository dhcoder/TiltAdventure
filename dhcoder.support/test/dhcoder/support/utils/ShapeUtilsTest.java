package dhcoder.support.utils;

import dhcoder.support.collision.shape.Circle;
import dhcoder.support.collision.shape.Rectangle;
import org.junit.Test;

import static dhcoder.support.utils.ShapeUtils.testIntersection;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

public final class ShapeUtilsTest {

    @Test
    public void testCircleIntersectsWithCircle() {
        // Circle1: W, Circle2: E, overlaps with penetration
        assertThat(testIntersection(new Circle(-2f, 0f, 3f), new Circle(2f, 0f, 2f)), equalTo(true));

        // Circle1: N, Circle2: S, overlaps with penetration
        assertThat(testIntersection(new Circle(-5f, 10f, 3f), new Circle(-5f, 2f, 5f)), equalTo(true));

        // TODO: Overlaps barely, contains, doesn't overlap
    }

    @Test
    public void testRectangleIntersectsWithRectangle() {
        // Circle1: W, Circle2: E, overlaps
        assertThat(testIntersection(new Rectangle(-2f, 0f, 4f, 4f), new Rectangle(4f, 0f, 4f, 4f)), equalTo(true));
    }

    @Test
    public void testCircleIntersectsWithRectangle() {

    }

}