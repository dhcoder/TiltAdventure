package dhcoder.libgdx.collision.agent;

import com.badlogic.gdx.math.Vector2;
import dhcoder.libgdx.collision.shape.Shape;

/**
 * A collision agent which wraps another agent, reversing the input parameters being tested. So for example, reversing a
 * CircleRectangleAgent gives you a RectangleCircleAgent.
 */
public final class ReverseCollisionAgent implements CollisionAgent {

    private final CollisionAgent wrappedAgent;

    public ReverseCollisionAgent(final CollisionAgent wrappedAgent) {
        this.wrappedAgent = wrappedAgent;
    }

    @Override
    public boolean testIntersection(final Shape shape1, final float x1, final float y1, final Shape shape2,
        final float x2, final float y2) {
        return wrappedAgent.testIntersection(shape2, x2, y2, shape1, x1, y1);
    }

    @Override
    public void getRepulsion(final Shape shape1, final float fromX1, final float fromY1, final float toX1,
        final float toY1, final Shape shape2, final float fromX2, final float fromY2, final float toX2,
        final float toY2, final Vector2 outRepulsion) {

        wrappedAgent.getRepulsion(shape2, fromX2, fromY2, toX2, toY2, shape1, fromX1, fromY1, toX1, toY1, outRepulsion);
        outRepulsion.scl(-1f, -1f);
    }
}
