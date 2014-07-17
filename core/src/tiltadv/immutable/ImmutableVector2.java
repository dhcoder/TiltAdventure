package tiltadv.immutable;

import com.badlogic.gdx.math.Vector2;

/**
 * A class that wraps a Vector2, offering read-only access to it.
 */
public class ImmutableVector2 extends Immutable<Vector2> {

    public ImmutableVector2(final Vector2 vector2) {
        super(vector2);
    }

    @Override
    public Vector2 toMutable() {
        return wrappedMutable.cpy();
    }

    public float getX() {
        return wrappedMutable.x;
    }

    public float getY() {
        return wrappedMutable.y;
    }

    public float getAngle() {
        return wrappedMutable.angle();
    }

    public float getAngleRad() {
        return wrappedMutable.angleRad();
    }

    public boolean isUnit() {
        return wrappedMutable.isUnit();
    }

    public boolean isUnit(final float margin) {
        return wrappedMutable.isUnit(margin);
    }

    public boolean isZero() {
        return wrappedMutable.isZero();
    }

    public boolean isZero(final float margin) {
        return wrappedMutable.isZero(margin);
    }

    public boolean isOnLine(final Vector2 other) {
        return wrappedMutable.isOnLine(other);
    }

    public boolean isOnLine(final Vector2 other, final float epsilon) {
        return wrappedMutable.isOnLine(other, epsilon);
    }

    public boolean isOnLine(final ImmutableVector2 handle) {
        return wrappedMutable.isOnLine(handle.wrappedMutable);
    }

    public boolean isOnLine(final ImmutableVector2 handle, final float epsilon) {
        return wrappedMutable.isOnLine(handle.wrappedMutable, epsilon);
    }

    public boolean isCollinear(final Vector2 other) {
        return wrappedMutable.isCollinear(other);
    }

    public boolean isCollinear(final Vector2 other, final float epsilon) {
        return wrappedMutable.isCollinear(other, epsilon);
    }

    public boolean isCollinear(final ImmutableVector2 handle) {
        return wrappedMutable.isCollinear(handle.wrappedMutable);
    }

    public boolean isCollinear(final ImmutableVector2 handle, final float epsilon) {
        return wrappedMutable.isCollinear(handle.wrappedMutable, epsilon);
    }

    public boolean isCollinearOpposite(final Vector2 other) {
        return wrappedMutable.isCollinearOpposite(other);
    }

    public boolean isCollinearOpposite(final Vector2 other, final float epsilon) {
        return wrappedMutable.isCollinearOpposite(other, epsilon);
    }

    public boolean isCollinearOpposite(final ImmutableVector2 handle) {
        return wrappedMutable.isCollinearOpposite(handle.wrappedMutable);
    }

    public boolean isCollinearOpposite(final ImmutableVector2 handle, final float epsilon) {
        return wrappedMutable.isCollinearOpposite(handle.wrappedMutable, epsilon);
    }

    public boolean isPerpendicular(final Vector2 other) {
        return wrappedMutable.isPerpendicular(other);
    }

    public boolean isPerpendicular(final Vector2 other, final float epsilon) {
        return wrappedMutable.isPerpendicular(other, epsilon);
    }

    public boolean isPerpendicular(final ImmutableVector2 handle) {
        return wrappedMutable.isPerpendicular(handle.wrappedMutable);
    }

    public boolean isPerpendicular(final ImmutableVector2 handle, final float epsilon) {
        return wrappedMutable.isPerpendicular(handle.wrappedMutable, epsilon);
    }

    public boolean hasSameDirection(final Vector2 other) {
        return wrappedMutable.hasSameDirection(other);
    }

    public boolean hasSameDirection(final ImmutableVector2 handle) {
        return wrappedMutable.hasSameDirection(handle.wrappedMutable);
    }

    public boolean hasOppositeDirection(final Vector2 other) {
        return wrappedMutable.hasOppositeDirection(other);
    }

    public boolean hasOppositeDirection(final ImmutableVector2 handle) {
        return wrappedMutable.hasOppositeDirection(handle.wrappedMutable);
    }

    public float len() {
        return wrappedMutable.len();
    }

    public float len2() {
        return wrappedMutable.len2();
    }

    public float dot(final float x, final float y) {
        return wrappedMutable.dot(x, y);
    }

    public float dot(final Vector2 v) {
        return wrappedMutable.dot(v);
    }

    public float dot(final ImmutableVector2 handle) {
        return wrappedMutable.dot(handle.wrappedMutable);
    }

    public float dst(final float x, final float y) {
        return wrappedMutable.dst(x, y);
    }

    public float dst(final Vector2 v) {
        return wrappedMutable.dst(v);
    }

    public float dst(final ImmutableVector2 handle) {
        return wrappedMutable.dst(handle.wrappedMutable);
    }

    public float dst2(final float x, final float y) {
        return wrappedMutable.dst2(x, y);
    }

    public float dst2(final Vector2 v) {
        return wrappedMutable.dst2(v);
    }

    public float dst2(final ImmutableVector2 handle) {
        return wrappedMutable.dst2(handle.wrappedMutable);
    }

    public float crs(final float x, final float y) {
        return wrappedMutable.crs(x, y);
    }

    public float crs(final Vector2 v) {
        return wrappedMutable.crs(v);
    }

    public float crs(final ImmutableVector2 handle) {
        return wrappedMutable.crs(handle.wrappedMutable);
    }

    public boolean epsilonEquals(final float x, final float y, final float epsilon) {
        return wrappedMutable.epsilonEquals(x, y, epsilon);
    }

    public boolean epsilonEquals(final Vector2 v, final float epsilon) {
        return wrappedMutable.epsilonEquals(v, epsilon);
    }

    public boolean epsilonEquals(final ImmutableVector2 handle, final float epsilon) {
        return wrappedMutable.epsilonEquals(handle.wrappedMutable, epsilon);
    }

    @Override
    public int hashCode() {
        return wrappedMutable.hashCode();
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }

        ImmutableVector2 that = (ImmutableVector2)o;

        if (!wrappedMutable.equals(that.wrappedMutable)) { return false; }

        return true;
    }
}
