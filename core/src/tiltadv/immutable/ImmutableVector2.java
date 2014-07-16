package tiltadv.immutable;

import com.badlogic.gdx.math.Vector2;

/**
 * A class that wraps a Vector2, offering read-only access to it.
 */
public class ImmutableVector2 {

    private final Vector2 innerVector2;

    public ImmutableVector2(final Vector2 vector2) {
        this.innerVector2 = vector2;
    }

    public Vector2 toVector2() {
        return innerVector2.cpy();
    }

    public float getX() {
        return innerVector2.x;
    }

    public float getY() {
        return innerVector2.y;
    }

    public float getAngle() {
        return innerVector2.angle();
    }

    public float getAngleRad() {
        return innerVector2.angleRad();
    }

    public boolean isUnit() {
        return innerVector2.isUnit();
    }

    public boolean isUnit(final float margin) {
        return innerVector2.isUnit(margin);
    }

    public boolean isZero() {
        return innerVector2.isZero();
    }

    public boolean isZero(final float margin) {
        return innerVector2.isZero(margin);
    }

    public boolean isOnLine(final Vector2 other) {
        return innerVector2.isOnLine(other);
    }

    public boolean isOnLine(final Vector2 other, final float epsilon) {
        return innerVector2.isOnLine(other, epsilon);
    }

    public boolean isOnLine(final ImmutableVector2 handle) {
        return innerVector2.isOnLine(handle.innerVector2);
    }

    public boolean isOnLine(final ImmutableVector2 handle, final float epsilon) {
        return innerVector2.isOnLine(handle.innerVector2, epsilon);
    }

    public boolean isCollinear(final Vector2 other) {
        return innerVector2.isCollinear(other);
    }

    public boolean isCollinear(final Vector2 other, final float epsilon) {
        return innerVector2.isCollinear(other, epsilon);
    }

    public boolean isCollinear(final ImmutableVector2 handle) {
        return innerVector2.isCollinear(handle.innerVector2);
    }

    public boolean isCollinear(final ImmutableVector2 handle, final float epsilon) {
        return innerVector2.isCollinear(handle.innerVector2, epsilon);
    }

    public boolean isCollinearOpposite(final Vector2 other) {
        return innerVector2.isCollinearOpposite(other);
    }

    public boolean isCollinearOpposite(final Vector2 other, final float epsilon) {
        return innerVector2.isCollinearOpposite(other, epsilon);
    }

    public boolean isCollinearOpposite(final ImmutableVector2 handle) {
        return innerVector2.isCollinearOpposite(handle.innerVector2);
    }

    public boolean isCollinearOpposite(final ImmutableVector2 handle, final float epsilon) {
        return innerVector2.isCollinearOpposite(handle.innerVector2, epsilon);
    }

    public boolean isPerpendicular(final Vector2 other) {
        return innerVector2.isPerpendicular(other);
    }

    public boolean isPerpendicular(final Vector2 other, final float epsilon) {
        return innerVector2.isPerpendicular(other, epsilon);
    }

    public boolean isPerpendicular(final ImmutableVector2 handle) {
        return innerVector2.isPerpendicular(handle.innerVector2);
    }

    public boolean isPerpendicular(final ImmutableVector2 handle, final float epsilon) {
        return innerVector2.isPerpendicular(handle.innerVector2, epsilon);
    }

    public boolean hasSameDirection(final Vector2 other) {
        return innerVector2.hasSameDirection(other);
    }

    public boolean hasSameDirection(final ImmutableVector2 handle) {
        return innerVector2.hasSameDirection(handle.innerVector2);
    }

    public boolean hasOppositeDirection(final Vector2 other) {
        return innerVector2.hasOppositeDirection(other);
    }

    public boolean hasOppositeDirection(final ImmutableVector2 handle) {
        return innerVector2.hasOppositeDirection(handle.innerVector2);
    }

    public float len() {
        return innerVector2.len();
    }

    public float len2() {
        return innerVector2.len2();
    }

    public float dst(final float x, final float y) {
        return innerVector2.dst(x, y);
    }

    public float dst(final Vector2 v) {
        return innerVector2.dst(v);
    }

    public float dst(final ImmutableVector2 handle) {
        return innerVector2.dst(handle.innerVector2);
    }

    public float dst2(final float x, final float y) {
        return innerVector2.dst2(x, y);
    }

    public float dst2(final Vector2 v) {
        return innerVector2.dst2(v);
    }

    public float dst2(final ImmutableVector2 handle) {
        return innerVector2.dst2(handle.innerVector2);
    }

    public float crs(final float x, final float y) {
        return innerVector2.crs(x, y);
    }

    public float crs(final Vector2 v) {
        return innerVector2.crs(v);
    }

    public float crs(final ImmutableVector2 handle) {
        return innerVector2.crs(handle.innerVector2);
    }

    public boolean epsilonEquals(final float x, final float y, final float epsilon) {
        return innerVector2.epsilonEquals(x, y, epsilon);
    }

    public boolean epsilonEquals(final Vector2 v, final float epsilon) {
        return innerVector2.epsilonEquals(v, epsilon);
    }

    public boolean epsilonEquals(final ImmutableVector2 handle, final float epsilon) {
        return innerVector2.epsilonEquals(handle.innerVector2, epsilon);
    }

    @Override
    public int hashCode() {
        return innerVector2.hashCode();
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }

        ImmutableVector2 that = (ImmutableVector2)o;

        if (!innerVector2.equals(that.innerVector2)) { return false; }

        return true;
    }

    @Override
    public String toString() {
        return innerVector2.toString();
    }
}
