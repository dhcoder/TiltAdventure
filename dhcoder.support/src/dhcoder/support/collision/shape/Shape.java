package dhcoder.support.collision.shape;

/**
 * Base class for shapes.
 */
public interface Shape {

    /**
     * Sets the X,Y origin coordinate of this shape.
     */
    void setOrigin(float x, float y);

    /**
     * Returns the origin X coordinate of this shape.
     */
    float getX();

    /**
     * Returns the origin Y coordinate of this shape.
     */
    float getY();

    boolean containsPoint(float x, float y);

    /**
     * Reset this shape to one that's centered at the origin with no size.
     */
    void reset();
}
