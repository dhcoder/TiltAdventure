package dhcoder.support.collision.shape;

import static dhcoder.support.utils.StringUtils.format;

/**
 * Class that represents a circle
 */
public final class Rectangle implements Shape {

    private float x;
    private float y;
    private float halfWidth;
    private float halfHeight;

    /**
     * Creates a rectangle by specifying its original x,y point and half-width and heights.
     */
    public Rectangle(final float x, final float y, final float halfWidth, final float halfHeight) {

        setOrigin(x, y);
        setHalfSize(halfWidth, halfHeight);
    }

    public void setOrigin(final float x, final float y) {
        this.x = x;
        this.y = y;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public void setHalfSize(final float halfWidth, final float halfHeight) {
        if (halfWidth < 0f || halfHeight < 0f) {
            throw new IllegalArgumentException(
                format("Can't create rectangle with < 0 half-size ({0},{1})", halfWidth, halfHeight));
        }

        this.halfWidth = halfWidth;
        this.halfHeight = halfHeight;
    }

    public float getHalfWidth() {
        return halfWidth;
    }

    public float getHalfHeight() {
        return halfHeight;
    }

    @Override
    public boolean containsPoint(final float x, final float y) {
        float deltaX = x - this.x;
        float deltaY = y - this.y;

        return ((deltaX * deltaX <= halfWidth * halfWidth) && (deltaY * deltaY <= halfHeight * halfHeight));
    }
}
