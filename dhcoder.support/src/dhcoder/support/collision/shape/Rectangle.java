package dhcoder.support.collision.shape;

import static dhcoder.support.utils.StringUtils.format;

/**
 * Class that represents a circle
 */
public final class Rectangle implements Shape {

    private float halfWidth;
    private float halfHeight;

    public Rectangle() {
        reset();
    }

    /**
     * Creates a rectangle by specifying its original x,y point and half-width and heights.
     */
    public Rectangle(final float halfWidth, final float halfHeight) {
        setHalfSize(halfWidth, halfHeight);
    }

    @Override
    public boolean containsPoint(final float x, final float y) {
        return Math.abs(x) < halfWidth && Math.abs(y) < halfHeight;
    }

    @Override
    public void reset() {
        halfWidth = halfHeight = 0f;
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

    public float getLeft(final float xOrigin) { return xOrigin - halfWidth; }

    public float getBottom(final float yOrigin) { return yOrigin - halfHeight; }

    public float getRight(final float xOrigin) { return xOrigin + halfWidth; }

    public float getTop(final float yOrigin) { return yOrigin + halfHeight; }
}
