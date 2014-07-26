package dhcoder.support.collision.shape;

import dhcoder.support.lambda.Action;
import org.junit.Test;

import static dhcoder.test.TestUtils.assertException;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

public final class SquareTest {

    @Test
    public void testGetters() {
        Square square = new Square(1f, 2f, 3f);
        assertThat(square.getX(), equalTo(1f));
        assertThat(square.getY(), equalTo(2f));
        assertThat(square.getHalfSize(), equalTo(3f));

        square.setOrigin(-1f, -2f);
        square.setHalfSize(4f);
        assertThat(square.getX(), equalTo(-1f));
        assertThat(square.getY(), equalTo(-2f));
        assertThat(square.getHalfSize(), equalTo(4f));
    }

    @Test
    public void testContainsPoint() {
        Square square = new Square(0f, 0f, 5f);

        assertThat(square.containsPoint(0f, 0f), equalTo(true));
        assertThat(square.containsPoint(5f, 5f), equalTo(true));
        assertThat(square.containsPoint(-5f, -5f), equalTo(true));
        assertThat(square.containsPoint(-5f, 5f), equalTo(true));
        assertThat(square.containsPoint(5f, -5f), equalTo(true));
        assertThat(square.containsPoint(1f, 2f), equalTo(true));

        assertThat(square.containsPoint(6f, 4f), equalTo(false));
        assertThat(square.containsPoint(2f, 10f), equalTo(false));
    }

    @Test
    public void testAsRectangle() {
        Square square = new Square(0f, 0f, 5f);
        Rectangle rectangle = square.asRectangle();

        assertThat(rectangle.getHalfWidth(), equalTo(5f));
        assertThat(rectangle.getHalfHeight(), equalTo(5f));
    }

    @Test
    public void invalidSizeThrowsException() {

        assertException("Can't create a square with negative size", IllegalArgumentException.class, new Action() {
            @Override
            public void run() {
                new Square(0f, 0f, -5f);
            }
        });
    }

}