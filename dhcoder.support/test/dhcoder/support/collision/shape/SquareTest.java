package dhcoder.support.collision.shape;

import dhcoder.support.lambda.Action;
import org.junit.Test;

import static dhcoder.test.TestUtils.assertException;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

public final class SquareTest {

    @Test
    public void testSquareContainsPoint() {
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
    public void invalidSizeThrowsException() {

        assertException("Can't create a square with 0 size", IllegalArgumentException.class, new Action() {
            @Override
            public void run() {
                new Square(0f, 0f, 0f);
            }
        });

        assertException("Can't create a square with negative size", IllegalArgumentException.class, new Action() {
            @Override
            public void run() {
                new Square(0f, 0f, -5f);
            }
        });
    }

}