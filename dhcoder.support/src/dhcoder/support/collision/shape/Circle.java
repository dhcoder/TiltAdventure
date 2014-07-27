package dhcoder.support.collision.shape;

import static dhcoder.support.utils.StringUtils.format;

/**
 * Class that represents a circle
 */
public final class Circle implements Shape {

    private float x;
    private float y;
    private float radius;

    public Circle() {
        reset();
    }

    public Circle(final float x, final float y, final float radius) {

        if (radius < 0f) {
            throw new IllegalArgumentException(format("Can't create circle with < 0 radius {0}", radius));
        }

        this.x = x;
        this.y = y;
        this.radius = radius;
    }

    @Override
    public void setOrigin(final float x, final float y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public float getX() {
        return x;
    }

    @Override
    public float getY() {
        return y;
    }

    @Override
    public boolean containsPoint(final float x, final float y) {
        float deltaX = x - this.x;
        float deltaY = y - this.y;
        return (deltaX * deltaX + deltaY * deltaY) <= radius * radius;
    }

    @Override
    public void reset() {
        x = y = radius = 0f;
    }

    public float getRadius() {
        return radius;
    }

    public void setRadius(final float radius) {
        this.radius = radius;
    }

    @Override
    public float getX0() {
        return x - radius;
    }

    @Override
    public float getY0() {
        return y - radius;
    }

    @Override
    public float getX1() {
        return x + radius;
    }

    @Override
    public float getY1() {
        return y + radius;
    }
}
