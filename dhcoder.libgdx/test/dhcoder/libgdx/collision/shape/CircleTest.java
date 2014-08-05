package dhcoder.libgdx.collision.shape;

import org.junit.Test;

import static dhcoder.test.TestUtils.assertException;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

public final class CircleTest {

    @Test
    public void testContainsPoint() {
        Circle circle = new Circle(2f);
        assertThat(circle.containsPoint(0f, 0f), equalTo(true));
        assertThat(circle.containsPoint(1f, 0f), equalTo(true));
        assertThat(circle.containsPoint(0f, 1f), equalTo(true));
        assertThat(circle.containsPoint(-1f, 0f), equalTo(true));
        assertThat(circle.containsPoint(0f, -1f), equalTo(true));
        assertThat(circle.containsPoint(0.3f, 0.4f), equalTo(true));

        assertThat(circle.containsPoint(2f, 2f), equalTo(false));
        assertThat(circle.containsPoint(2f, 0f), equalTo(true));
        assertThat(circle.containsPoint(0f, 2f), equalTo(true));
        assertThat(circle.containsPoint(-2f, 0f), equalTo(true));
        assertThat(circle.containsPoint(0f, -2f), equalTo(true));
    }

    @Test
    public void invalidRadiusThrowsException() {
        assertException("Can't create a circle with negative radius", IllegalArgumentException.class, new Runnable() {
            @Override
            public void run() {
                new Circle(-5f);
            }
        });
    }
}