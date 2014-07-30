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
        // Circle1: W, Circle2: E, overlaps
        assertThat(testIntersection(new Circle(-2f, -2f, 3f), new Circle(2f, -2f, 2f)), equalTo(true));

        // Circle1: N, Circle2: S, overlaps
        assertThat(testIntersection(new Circle(-5f, 10f, 3f), new Circle(-5f, 2f, 5f)), equalTo(true));

        // Circle1: NW, Circle2: SE, overlaps
        assertThat(testIntersection(new Circle(10f, 10f, 3f), new Circle(15f, 9f, 4f)), equalTo(true));

        // Circle1: NE, Circle2: SW, overlaps
        assertThat(testIntersection(new Circle(10f, 10f, 4f), new Circle(6f, 6f, 3f)), equalTo(true));

        // Circle1 contains Circle2
        assertThat(testIntersection(new Circle(0f, 0f, 5f), new Circle(0f, 0f, 1f)), equalTo(true));

        // Circle1: W, Circle2: E, doesn't overlap
        assertThat(testIntersection(new Circle(-2f, -2f, 3f), new Circle(8f, -2f, 2f)), equalTo(false));

        // Circle1: N, Circle2: S, doesn't overlap
        assertThat(testIntersection(new Circle(-5f, 10f, 3f), new Circle(-5f, -4f, 5f)), equalTo(false));

        // Circle1: NW, Circle2: SE, doesn't overlap
        assertThat(testIntersection(new Circle(10f, 10f, 3f), new Circle(15f, 15f, 1f)), equalTo(false));

        // Circle1: NE, Circle2: SW, doesn't overlap
        assertThat(testIntersection(new Circle(10f, 10f, 3f), new Circle(5f, 5f, 1f)), equalTo(false));
    }

    @Test
    public void testRectangleIntersectsWithRectangle() {
        // Rect1: W, Rect2: E, overlaps
        assertThat(testIntersection(new Rectangle(-2f, 0f, 4f, 4f), new Rectangle(4f, 0f, 4f, 4f)), equalTo(true));

        // Rect1: N, Rect2: S, overlaps
        assertThat(testIntersection(new Rectangle(5f, 10f, 4f, 4f), new Rectangle(5f, 3f, 4f, 4f)), equalTo(true));

        // Rect1: NW, Rect2: SE, overlaps
        assertThat(testIntersection(new Rectangle(10f, 10f, 2f, 3f), new Rectangle(15f, 5f, 4f, 4f)), equalTo(true));

        // Rect1: NE, Rect2: SW, overlaps
        assertThat(testIntersection(new Rectangle(10f, 10f, 2f, 3f), new Rectangle(5f, 5f, 4f, 4f)), equalTo(true));

        // Rect1 and Rect2 in plus configuration
        assertThat(testIntersection(new Rectangle(5f, 5f, 5f, 2f), new Rectangle(6f, 4f, 1f, 5f)), equalTo(true));

        // Rect1 contains Rect2
        assertThat(testIntersection(new Rectangle(5f, 5f, 5f, 2f), new Rectangle(5f, 5f, 3f, 1f)), equalTo(true));
    }

    @Test
    public void testCircleIntersectsWithRectangle() {
        // TODO: Stop being lazy and write these tests
    }

}