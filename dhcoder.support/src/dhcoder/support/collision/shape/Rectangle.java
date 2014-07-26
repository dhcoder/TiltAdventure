package dhcoder.support.collision.shape;

import static dhcoder.support.utils.StringUtils.format;

/**
 * Class that represents a circle
 */
public final class Rectangle implements Shape {

    private final float xCenter;
    private final float yCenter;
    private final float halfWidth;
    private final float halfHeight;

    public Rectangle(final float xCenter, final float yCenter, final float halfWidth, final float halfHeight) {

        if (halfWidth <= 0f || halfHeight <= 0f) {
            throw new IllegalArgumentException(
                format("Can't create rectangle with <= 0 size ({0},{1})", halfWidth * 2, halfHeight * 2));
        }

        this.xCenter = xCenter;
        this.yCenter = yCenter;
        this.halfWidth = halfWidth;
        this.halfHeight = halfHeight;
    }

    @Override
    public boolean containsPoint(final float x, final float y) {
        float deltaX = x - this.xCenter;
        float deltaY = y - this.yCenter;

        return ((deltaX * deltaX <= halfWidth * halfWidth) && (deltaY * deltaY <= halfHeight * halfHeight));
    }
}
