package dhcoder.support.collision.shape;

import static dhcoder.support.utils.StringUtils.format;

/**
 * Class that represents a circle
 */
public final class Square implements Shape {

    private final Rectangle rectangle;

    /**
     * Creates a square by specifying its original x,y point and a half-size.
     */
    public Square(final float x, final float y, final float halfSize) {

        if (halfSize <= 0f) {
            throw new IllegalArgumentException(format("Can't create square with <= 0 size {0}", halfSize));
        }

        rectangle = new Rectangle(x, y, halfSize, halfSize);
    }

    public Rectangle asRectangle() {
        return rectangle;
    }

    public void setOrigin(final float x, final float y) {
        rectangle.setOrigin(x, y);
    }

    public float getX() {
        return rectangle.getX();
    }

    public float getY() {
        return rectangle.getY();
    }

    public void setHalfSize(final float halfSize) {
        rectangle.setHalfSize(halfSize, halfSize);
    }

    public float getHalfSize() {
        return rectangle.getHalfWidth();
    }

    @Override

    public boolean containsPoint(final float x, final float y) {
        return rectangle.containsPoint(x, y);
    }
}
