package dhcoder.libgdx.collision.agent;

import com.badlogic.gdx.math.Vector2;
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
     * Given two shapes, return the repulsion force felt on the first shape.
     */
    void getRepulsion(Shape shape1, float fromX1, float fromY1, float toX1, float toY1, Shape shape2, float fromX2,
        float fromY2, float toX2, float toY2, Vector2 outRepulsion);

    /**
     * Given two shapes that are right next to each other, return the normal between the two of them. The vector will be
     * of unit length. The normal will be associated with shape2.
     *
     * The fact that the two shapes should be next to each other lets us make some assumptions that help us make this
     * calculation quicker. If the two shapes are far apart, the normal value will be unreliable.
     */
    void getNormal(Shape shape1, float x1, float y1, Shape shape2, float x2, float y2, Vector2 outNormal);
}
