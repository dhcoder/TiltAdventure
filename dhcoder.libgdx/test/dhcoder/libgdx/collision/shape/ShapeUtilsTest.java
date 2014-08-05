package dhcoder.libgdx.collision.shape;

import org.junit.Test;

import static dhcoder.libgdx.collision.shape.ShapeUtils.testIntersection;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

public final class ShapeUtilsTest {

    @Test
    public void testCircleIntersectsWithCircle() {
        // Circle1: W, Circle2: E, overlaps
        assertThat(testIntersection(new Circle(3f), -2f, -2f, new Circle(2f), 2f, -2f), equalTo(true));

        // Circle1: N, Circle2: S, overlaps
        assertThat(testIntersection(new Circle(4f), -5f, 10f, new Circle(5f), -5f, 2f), equalTo(true));

        // Circle1: NW, Circle2: SE, overlaps
        assertThat(testIntersection(new Circle(3f), 10f, 10f, new Circle(4f), 15f, 9f), equalTo(true));

        // Circle1: NE, Circle2: SW, overlaps
        assertThat(testIntersection(new Circle(4f), 10f, 10f, new Circle(3f), 6f, 6f), equalTo(true));

        // Circle1 contains Circle2
        assertThat(testIntersection(new Circle(5f), 0f, 0f, new Circle(1f), 0f, 0f), equalTo(true));

        // Circle1: W, Circle2: E, doesn't overlap
        assertThat(testIntersection(new Circle(3f), -2f, -2f, new Circle(2f), 8f, -2f), equalTo(false));

        // Circle1: N, Circle2: S, doesn't overlap
        assertThat(testIntersection(new Circle(3f), -5f, 10f, new Circle(5f), -5f, -4f), equalTo(false));

        // Circle1: NW, Circle2: SE, doesn't overlap
        assertThat(testIntersection(new Circle(3f), 10f, 10f, new Circle(1f), 15f, 15f), equalTo(false));

        // Circle1: NE, Circle2: SW, doesn't overlap
        assertThat(testIntersection(new Circle(3f), 10f, 10f, new Circle(1f), 5f, 5f), equalTo(false));
    }

    @Test
    public void testRectangleIntersectsWithRectangle() {
        // Rect1: W, Rect2: E, overlaps
        assertThat(testIntersection(new Rectangle(4f, 4f), 2f, 0f, new Rectangle(4f, 4f), 8f, 0f), equalTo(true));

        // Rect1: N, Rect2: S, overlaps
        assertThat(testIntersection(new Rectangle(4f, 4f), 5f, 10f, new Rectangle(4f, 4f), 5f, 3f), equalTo(true));

        // Rect1: NW, Rect2: SE, overlaps
        assertThat(testIntersection(new Rectangle(2f, 3f), 10f, 10f, new Rectangle(4f, 4f), 15f, 5f), equalTo(true));

        // Rect1: NE, Rect2: SW, overlaps
        assertThat(testIntersection(new Rectangle(2f, 3f), 10f, 10f, new Rectangle(4f, 4f), 5f, 5f), equalTo(true));

        // Rect1 and Rect2 in plus configuration
        assertThat(testIntersection(new Rectangle(5f, 2f), 5f, 5f, new Rectangle(1f, 5f), 6f, 4f), equalTo(true));

        // Rect1 contains Rect2
        assertThat(testIntersection(new Rectangle(5f, 2f), 5f, 5f, new Rectangle(3f, 1f), 5f, 5f), equalTo(true));

        // Rect1: W, Rect2: E, doesn't overlap
        assertThat(testIntersection(new Rectangle(2f, 3f), -2f, 0f, new Rectangle(3f, 4f), 4f, 0f), equalTo(false));

        // Rect1: N, Rect2: S, doesn't overlap
        assertThat(testIntersection(new Rectangle(4f, 4f), 5f, 10f, new Rectangle(4f, 2f), 5f, 3f), equalTo(false));

        // Rect1: NW, Rect2: SE, doesn't overlap
        assertThat(testIntersection(new Rectangle(2f, 3f), 10f, 10f, new Rectangle(2f, 2f), 15f, 5f), equalTo(false));

        // Rect1: NE, Rect2: SW, doesn't overlap
        assertThat(testIntersection(new Rectangle(2f, 3f), 10f, 10f, new Rectangle(2f, 2f), 5f, 5f), equalTo(false));
    }

    @Test
    public void testCircleIntersectsWithRectangle() {
        // TODO: Stop being lazy and write these tests
        /*
        // Circle: W, Rect: E, overlaps
        assertThat(testIntersection(new Rectangle(-2f, 0f, 4f, 4f), new Rectangle(4f, 0f, 4f, 4f)), equalTo(true));

        // Circle: E, Rect: W, overlaps
        assertThat(testIntersection(new Rectangle(-2f, 0f, 4f, 4f), new Rectangle(4f, 0f, 4f, 4f)), equalTo(true));

        // Circle: N, Rect: S, overlaps
        assertThat(testIntersection(new Rectangle(5f, 10f, 4f, 4f), new Rectangle(5f, 3f, 4f, 4f)), equalTo(true));

        // Circle: S, Rect: N, overlaps
        assertThat(testIntersection(new Rectangle(5f, 10f, 4f, 4f), new Rectangle(5f, 3f, 4f, 4f)), equalTo(true));

        // Circle: NW, Rect: SE, overlaps
        assertThat(testIntersection(new Rectangle(10f, 10f, 2f, 3f), new Rectangle(15f, 5f, 4f, 4f)), equalTo(true));

        // Circle: SE, Rect: NW, overlaps
        assertThat(testIntersection(new Rectangle(10f, 10f, 2f, 3f), new Rectangle(15f, 5f, 4f, 4f)), equalTo(true));

        // Circle: NE, Rect: SW, overlaps
        assertThat(testIntersection(new Rectangle(10f, 10f, 2f, 3f), new Rectangle(5f, 5f, 4f, 4f)), equalTo(true));

        // Circle: SW, Rect: NE, overlaps
        assertThat(testIntersection(new Rectangle(10f, 10f, 2f, 3f), new Rectangle(5f, 5f, 4f, 4f)), equalTo(true));

        // Rect1 and Rect in plus configuration
        assertThat(testIntersection(new Rectangle(5f, 5f, 5f, 2f), new Rectangle(6f, 4f, 1f, 5f)), equalTo(true));

        // Rect1 contains Rect
        assertThat(testIntersection(new Rectangle(5f, 5f, 5f, 2f), new Rectangle(5f, 5f, 3f, 1f)), equalTo(true));

        // Circle: W, Rect: E, doesn't overlap
        assertThat(testIntersection(new Rectangle(-2f, 0f, 2f, 3f), new Rectangle(4f, 0f, 3f, 4f)), equalTo(false));

        // Circle: N, Rect: S, doesn't overlap
        assertThat(testIntersection(new Rectangle(5f, 10f, 4f, 4f), new Rectangle(5f, 3f, 4f, 2f)), equalTo(false));

        // Circle: NW, Rect: SE, doesn't overlap
        assertThat(testIntersection(new Rectangle(10f, 10f, 2f, 3f), new Rectangle(15f, 5f, 2f, 2f)), equalTo(false));

        // Circle: NE, Rect: SW, doesn't overlap
        assertThat(testIntersection(new Rectangle(10f, 10f, 2f, 3f), new Rectangle(5f, 5f, 2f, 2f)), equalTo(false));
        */
    }
}
