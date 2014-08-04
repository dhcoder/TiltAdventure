package dhcoder.support.utils;

import dhcoder.support.collection.Key2;
import dhcoder.support.collision.Intersection;
import dhcoder.support.collision.agent.CircleCollisionAgent;
import dhcoder.support.collision.agent.CircleRectangleCollisionAgent;
import dhcoder.support.collision.agent.CollisionAgent;
import dhcoder.support.collision.agent.RectangleCollisionAgent;
import dhcoder.support.collision.shape.Circle;
import dhcoder.support.collision.shape.Rectangle;
import dhcoder.support.collision.shape.Shape;
import dhcoder.support.memory.Pool;

import java.util.HashMap;
import java.util.Map;

import static dhcoder.support.utils.StringUtils.format;

public final class ShapeUtils {

    private static final class ShapeKey extends Key2<Class<? extends Shape>, Class<? extends Shape>> {
        private ShapeKey() { /* used by Pool */ }

        public ShapeKey(final Class<? extends Shape> value1, final Class<? extends Shape> value2) {
            super(value1, value2);
        }
    }

    private static final Map<ShapeKey, CollisionAgent> INTERSECTION_AGENTS = new HashMap<ShapeKey, CollisionAgent>();
    private static final Pool<ShapeKey> shapeKeyPool = Pool.of(ShapeKey.class, 1);

    static {
        INTERSECTION_AGENTS.put(new ShapeKey(Circle.class, Circle.class), new CircleCollisionAgent());
        INTERSECTION_AGENTS.put(new ShapeKey(Rectangle.class, Rectangle.class), new RectangleCollisionAgent());
        INTERSECTION_AGENTS.put(new ShapeKey(Circle.class, Rectangle.class), new CircleRectangleCollisionAgent());
        INTERSECTION_AGENTS.put(new ShapeKey(Rectangle.class, Circle.class), CircleRectangleCollisionAgent.reverse());
    }

    public static boolean testIntersection(final Shape shape1, final float x1, final float y1, final Shape shape2,
        final float x2, final float y2) {

        CollisionAgent agent = getCollisionAgent(shape1, shape2);
        return agent.testIntersection(shape1, x1, y1, shape2, x2, y2);
    }

    public static void getIntersection(final Shape shape1, final float fromX1, final float fromY1, final float toX1,
        final float toY1, final Shape shape2, final float fromX2, final float fromY2, final float toX2,
        final float toY2, final Intersection outIntersection) {

        CollisionAgent agent = getCollisionAgent(shape1, shape2);
        agent.getIntersection(shape1, fromX1, fromY1, toX1, toY1, shape2, fromX2, fromY2, toX2, toY2, outIntersection);
    }

    private static CollisionAgent getCollisionAgent(final Shape shape1, final Shape shape2) {
        ShapeKey key = shapeKeyPool.grabNew();
        key.set(shape1.getClass(), shape2.getClass());
        if (!INTERSECTION_AGENTS.containsKey(key)) {
            shapeKeyPool.free(key);

            throw new IllegalArgumentException(
                format("Unexpected shapes passed in for intersection testing: {0} & {1}", shape1.getClass(),
                    shape2.getClass()));
        }

        CollisionAgent agent = INTERSECTION_AGENTS.get(key);
        shapeKeyPool.free(key);

        return agent;
    }

    private ShapeUtils() {} // Disabled constructor

}
