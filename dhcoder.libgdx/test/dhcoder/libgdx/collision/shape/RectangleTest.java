package dhcoder.libgdx.collision.shape;

import org.junit.Test;

import static dhcoder.test.TestUtils.assertException;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

public final class RectangleTest {

    @Test
    public void testContainsPoint() {
        Rectangle rect = new Rectangle(4f, 3f);

        assertThat(rect.containsPoint(0f, 0f), equalTo(true));
        assertThat(rect.containsPoint(-3f, -2f), equalTo(true));
        assertThat(rect.containsPoint(3f, 2f), equalTo(true));
        assertThat(rect.containsPoint(-3f, 2f), equalTo(true));
        assertThat(rect.containsPoint(3f, -2f), equalTo(true));
        assertThat(rect.containsPoint(1f, 2f), equalTo(true));

        assertThat(rect.containsPoint(4f, 3f), equalTo(false));
        assertThat(rect.containsPoint(4f, 4f), equalTo(false));
        assertThat(rect.containsPoint(2f, 10f), equalTo(false));
    }

    @Test
    public void invalidSizeThrowsException() {

        assertException("Can't create a circle with negative width", IllegalArgumentException.class, new Runnable() {
            @Override
            public void run() {
                new Rectangle(-10f, 10f);
            }
        });

        assertException("Can't create a circle with negative height", IllegalArgumentException.class, new Runnable() {
            @Override
            public void run() {
                new Rectangle(10f, -10f);
            }
        });
    }

}