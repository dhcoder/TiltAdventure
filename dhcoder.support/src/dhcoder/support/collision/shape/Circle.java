package dhcoder.support.collision.shape;

import static dhcoder.support.utils.StringUtils.format;

/**
 * Class that represents a circle
 */
public final class Circle implements Shape {

    private final float radius;
    private float x;
    private float y;

    public Circle(final float x, final float y, final float radius) {

        if (radius < 0f) {
            throw new IllegalArgumentException(format("Can't create circle with < 0 radius {0}", radius));
        }

        this.x = x;
        this.y = y;
        this.radius = radius;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public void setOrigin(final float x, final float y) {
        this.x = x;
        this.y = y;
    }

    public float getRadius() {
        return radius;
    }

    @Override
    public boolean containsPoint(final float x, final float y) {
        float deltaX = x - this.x;
        float deltaY = y - this.y;
        return (deltaX * deltaX + deltaY * deltaY) <= radius * radius;
    }
}
