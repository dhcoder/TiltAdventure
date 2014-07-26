package dhcoder.support.collision.shape;

import static dhcoder.support.utils.StringUtils.format;

/**
 * Class that represents a circle
 */
public final class Square implements Shape {

    private final Rectangle rectangle;

    public Square(final float xCenter, final float yCenter, final float halfSize) {

        if (halfSize <= 0f) {
            throw new IllegalArgumentException(format("Can't create square with <= 0 size {0}", halfSize));
        }

        rectangle = new Rectangle(xCenter, yCenter, halfSize, halfSize);
    }

    @Override
    public boolean containsPoint(final float x, final float y) {
        return rectangle.containsPoint(x, y);
    }
}
