package dhcoder.support.collision;

import dhcoder.support.math.Vec2;
import dhcoder.support.memory.Poolable;

/**
 * Class that contains information about the collision of two shapes.
 */
public final class Intersection implements Poolable {

    private final Vec2 sourcePosition = new Vec2();
    private final Vec2 targetPosition = new Vec2();
    private final Vec2 normalForce = new Vec2();

    public Intersection() {}

    /**
     * Returns the position of the source collider at the time of collision.
     */
    public Vec2 getSourcePosition() { return sourcePosition; }

    /**
     * Returns the position of the target collider at the time of collision.
     */
    public Vec2 getTargetPosition() { return targetPosition; }

    /**
     * Returns the normal force of the collision as felt by the source collider.
     * <p/>
     * If you add this vector force to the source position, you end up with a new final location that moves the
     * source collider forward in a way that avoids a collision. Multiplying this by (-1, -1) will return the force
     * felt by the target collider.
     */
    public Vec2 getNormalForce() { return normalForce; }

    public void set(final float sourceX, final float sourceY, final float targetX, final float targetY,
        final float normalX, final float normalY) {
        sourcePosition.set(sourceX, sourceY);
        targetPosition.set(targetX, targetY);
        normalForce.set(normalX, normalY);
    }

    @Override
    public void reset() {
        sourcePosition.reset();
        targetPosition.reset();
        normalForce.reset();
    }
}
