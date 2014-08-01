package dhcoder.support.collision.shape;

import static dhcoder.support.utils.StringUtils.format;

/**
 * Class that represents a circle
 */
public final class Circle implements Shape {

    private float radius;

    public Circle() {
        reset();
    }

    public Circle(final float radius) {

        if (radius < 0f) {
            throw new IllegalArgumentException(format("Can't create circle with < 0 radius {0}", radius));
        }

        setRadius(radius);
    }

    @Override
    public boolean containsPoint(final float x, final float y) {
        return (x * x + y * y) <= radius * radius;
    }

    @Override
    public void reset() {
        setRadius(0f);
    }

    public float getRadius() {
        return radius;
    }

    public void setRadius(final float radius) {
        this.radius = radius;
    }
}
