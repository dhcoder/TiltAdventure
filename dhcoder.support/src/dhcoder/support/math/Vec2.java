package dhcoder.support.math;

import dhcoder.support.memory.Poolable;

import static dhcoder.support.utils.StringUtils.format;

/**
 * 2D vector.
 */
public final class Vec2 implements Poolable {

    private float x;
    private float y;

    public Vec2() {
        this(0f, 0f);
    }

    public Vec2(final float x, final float y) {
        set(x, y);
    }

    public float getX() {
        return x;
    }

    public Vec2 setX(final float x) {
        this.x = x;
        return this;
    }

    public float getY() {
        return y;
    }

    public Vec2 setY(final float y) {
        this.y = y;
        return this;
    }

    public Vec2 set(final float x, final float y) {
        this.x = x;
        this.y = y;

        return this;
    }

    public Vec2 set(final Vec2 other) {
        return set(other.x, other.y);
    }

    public Vec2 add(final float x, final float y) {
        return set(this.x + x, this.y + y);
    }

    public Vec2 sub(final float x, final float y) {
        return set(this.x - x, this.y - y);
    }

    public Vec2 mul(final float x, final float y) {
        return set(this.x * x, this.y * y);
    }

    public Vec2 div(final float x, final float y) {
        return set(this.x / x, this.y / y);
    }

    public Vec2 add(final Vec2 other) {
        return add(other.x, other.y);
    }

    public Vec2 sub(final Vec2 other) {
        return sub(other.x, other.y);
    }

    public Vec2 mul(final Vec2 other) {
        return mul(other.x, other.y);
    }

    public Vec2 div(final Vec2 other) {
        return div(other.x, other.y);
    }

    public Vec2 scale(final float value) {
        return mul(value, value);
    }

    public Vec2 toUnit() {
        float len = len();
        if (len == 0f) {
            return this;
        }

        x /= len;
        y /= len;

        return this;
    }


    public float len() {
        return (float)Math.sqrt(len2());
    }

    public float len2() {
        return (x * x) + (y * y);
    }

    @Override
    public String toString() {
        return format("({0},{1})", x, y);
    }

    @Override
    public void reset() {
        set(0f, 0f);
    }

}