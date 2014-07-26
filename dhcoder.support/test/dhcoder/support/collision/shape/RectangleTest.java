package dhcoder.support.collision.shape;

import dhcoder.support.lambda.Action;
import org.junit.Test;

import static dhcoder.test.TestUtils.assertException;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

public final class RectangleTest {

    @Test
    public void testRectangleContainsPoint() {
        Rectangle rect = new Rectangle(0f, 0f, 4f, 3f);

        assertThat(rect.containsPoint(0f, 0f), equalTo(true));
        assertThat(rect.containsPoint(4f, 3f), equalTo(true));
        assertThat(rect.containsPoint(-4f, -3f), equalTo(true));
        assertThat(rect.containsPoint(-4f, 3f), equalTo(true));
        assertThat(rect.containsPoint(4f, -3f), equalTo(true));
        assertThat(rect.containsPoint(1f, 2f), equalTo(true));

        assertThat(rect.containsPoint(4f, 4f), equalTo(false));
        assertThat(rect.containsPoint(2f, 10f), equalTo(false));
    }

    @Test
    public void invalidSizeThrowsException() {

        assertException("Can't create a rectangle with 0 width", IllegalArgumentException.class, new Action() {
            @Override
            public void run() {
                new Rectangle(0f, 0f, 0f, 10f);
            }
        });

        assertException("Can't create a circle with negative width", IllegalArgumentException.class, new Action() {
            @Override
            public void run() {
                new Rectangle(0f, 0f, -10f, 10f);
            }
        });

        assertException("Can't create a rectangle with 0 height", IllegalArgumentException.class, new Action() {
            @Override
            public void run() {
                new Rectangle(0f, 0f, 10f, 0f);
            }
        });

        assertException("Can't create a circle with negative height", IllegalArgumentException.class, new Action() {
            @Override
            public void run() {
                new Rectangle(0f, 0f, 10f, -10f);
            }
        });
    }

}