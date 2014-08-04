package dhcoder.support.collision;

import dhcoder.support.memory.Poolable;

/**
 * Class that contains information about the collision of two shapes.
 */
public final class Intersection implements Poolable {

    // Location of colliding shapes at the time of intersection
    private float sourceX;
    private float sourceY;
    private float targetX;
    private float targetY;
    // The unit normal force relative to the source. That is, if the source comes in from the left and the target is
    // on the right, the normal force will be (-1, 0). The target's normal force is the flip of this, (1, 0).
    private float normalX;
    private float normalY;

    public Intersection() {}

    public float getSourceX() {
        return sourceX;
    }

    public float getSourceY() {
        return sourceY;
    }

    public float getTargetX() {
        return targetX;
    }

    public float getTargetY() {
        return targetY;
    }

    public float getNormalX() {
        return normalX;
    }

    public float getNormalY() {
        return normalY;
    }

    public void set(final float sourceX, final float sourceY, final float targetX, final float targetY,
        final float normalX, final float normalY) {
        this.sourceX = sourceX;
        this.sourceY = sourceY;
        this.targetX = targetX;
        this.targetY = targetY;
        this.normalX = normalX;
        this.normalY = normalY;
    }

    @Override
    public void reset() { set(0f, 0f, 0f, 0f, 0f, 0f); }
}
