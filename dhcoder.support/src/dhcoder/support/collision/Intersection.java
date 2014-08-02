package dhcoder.support.collision;

import dhcoder.support.memory.Poolable;

/**
 * Simple (x,y) data class.
 */
public final class Intersection implements Poolable {

    private float x;
    private float y;

    private Intersection() {}

    public Intersection(final float x, final float y) {
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

    @Override
    public void reset() {
        set(0f, 0f);
    }
}
