package dhcoder.libgdx.collision.agent;

import dhcoder.libgdx.collision.Intersection;
import dhcoder.libgdx.collision.shape.Shape;

/**
 * Interface that provides various collision operations between two shapes.
 */
public interface CollisionAgent {

    /**
     * Test if two shapes intersect, given locations of their origins.
     */
    boolean testIntersection(Shape shape1, float x1, float y1, Shape shape2, float x2, float y2);

    /**
     * Given two shapes and their start and end locations, set information about the point of contact into the
     * {@link Intersection} parameter.
     *
     * @throws IllegalArgumentException if it's discovered the two shapes don't actually collide.
     */
    void getIntersection(Shape shape1, float fromX1, float fromY1, float toX1, float toY1, Shape shape2, float fromX2,
        float fromY2, float toX2, float toY2, Intersection outIntersection);

}
