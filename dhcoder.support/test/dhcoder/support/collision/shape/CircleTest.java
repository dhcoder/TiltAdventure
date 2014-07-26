package dhcoder.support.collision.shape;

import dhcoder.support.lambda.Action;
import org.junit.Test;

import static dhcoder.test.TestUtils.assertException;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

public class CircleTest {

    @Test
    public void testCircleContainsPoint() {
        Circle circle = new Circle(0f, 0f, 1f);
        assertThat(circle.containsPoint(0f, 0f), equalTo(true));
        assertThat(circle.containsPoint(1f, 0f), equalTo(true));
        assertThat(circle.containsPoint(0f, 1f), equalTo(true));
        assertThat(circle.containsPoint(-1f, 0f), equalTo(true));
        assertThat(circle.containsPoint(0f, -1f), equalTo(true));
        assertThat(circle.containsPoint(0.3f, 0.4f), equalTo(true));

        assertThat(circle.containsPoint(1f, 1f), equalTo(false));
        assertThat(circle.containsPoint(0f, -2f), equalTo(false));
        assertThat(circle.containsPoint(.9f, .9f), equalTo(false));
    }

    @Test
    public void invalidRadiusThrowsException() {
        assertException("Can't create a circle with 0 radius", IllegalArgumentException.class, new Action() {
            @Override
            public void run() {
                new Circle(0f, 0f, 0f);
            }
        });

        assertException("Can't create a circle with negative radius", IllegalArgumentException.class, new Action() {
            @Override
            public void run() {
                new Circle(0f, 0f, -5f);
            }
        });
    }
}