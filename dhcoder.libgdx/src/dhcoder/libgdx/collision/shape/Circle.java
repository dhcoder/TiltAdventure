package dhcoder.libgdx.collision.shape;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import static dhcoder.support.text.StringUtils.format;

/**
 * Class that represents a circle
 */
public final class Circle implements Shape {

    private float radius;

    private Circle() {
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
    public void render(final ShapeRenderer renderer, final float x, final float y) {
        renderer.circle(x, y, radius);
    }

    @Override
    public String toString() {
        return format("r={0}", radius);
    }

    @Override
    public void reset() {
        setRadius(0f);
    }

    public float getRadius() {
        return radius;
    }

    @Override
    public float getLeft(final float xOrigin) { return xOrigin - radius; }

    @Override
    public float getBottom(final float yOrigin) { return yOrigin - radius; }

    @Override
    public float getRight(final float xOrigin) { return xOrigin + radius; }

    @Override
    public float getTop(final float yOrigin) { return yOrigin + radius; }

    @Override
    public float getHalfWidth() {
        return radius;
    }

    @Override
    public float getHalfHeight() {
        return radius;
    }

    public Circle setRadius(final float radius) {
        this.radius = radius;
        return this;
    }
}
