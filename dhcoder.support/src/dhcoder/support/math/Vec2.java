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

    public void setX(final float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(final float y) {
        this.y = y;
    }

    public void set(final float x, final float y) {
        this.x = x;
        this.y = y;
    }

    public void set(final Vec2 other) {
        set(other.x, other.y);
    }

    public void add(final float x, final float y) {
        set(this.x + x, this.y + y);
    }

    public void sub(final float x, final float y) {
        set(this.x - x, this.y - y);
    }

    public void mul(final float x, final float y) {
        set(this.x * x, this.y * y);
    }

    public void div(final float x, final float y) {
        set(this.x / x, this.y / y);
    }

    public void add(final Vec2 other) {
        add(other.x, other.y);
    }

    public void sub(final Vec2 other) {
        sub(other.x, other.y);
    }

    public void mul(final Vec2 other) {
        mul(other.x, other.y);
    }

    public void div(final Vec2 other) {
        div(other.x, other.y);
    }

    public void toUnit() {
        float len = len();
        if (len == 0f) {
            return;
        }

        x /= len;
        y /= len;
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