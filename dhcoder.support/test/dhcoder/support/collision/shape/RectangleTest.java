package dhcoder.support.collision.shape;

import org.junit.Test;

import static dhcoder.test.TestUtils.assertException;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

public final class RectangleTest {

    @Test
    public void testGetters() {
        Rectangle rectangle = new Rectangle(1f, 2f, 3f, 4f);
        assertThat(rectangle.getX(), equalTo(1f));
        assertThat(rectangle.getY(), equalTo(2f));
        assertThat(rectangle.getHalfWidth(), equalTo(3f));
        assertThat(rectangle.getHalfHeight(), equalTo(4f));

        rectangle.setOrigin(-1f, -2f);
        rectangle.setHalfSize(5f, 6f);
        assertThat(rectangle.getX(), equalTo(-1f));
        assertThat(rectangle.getY(), equalTo(-2f));
        assertThat(rectangle.getHalfWidth(), equalTo(5f));
        assertThat(rectangle.getHalfHeight(), equalTo(6f));
    }

    @Test
    public void testContainsPoint() {
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

        assertException("Can't create a circle with negative width", IllegalArgumentException.class, new Runnable() {
            @Override
            public void run() {
                new Rectangle(0f, 0f, -10f, 10f);
            }
        });

        assertException("Can't create a circle with negative height", IllegalArgumentException.class, new Runnable() {
            @Override
            public void run() {
                new Rectangle(0f, 0f, 10f, -10f);
            }
        });
    }

}