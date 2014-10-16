package dhcoder.libgdx.collision.shape;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import dhcoder.libgdx.collision.agent.CircleCollisionAgent;
import dhcoder.libgdx.collision.agent.CircleRectangleCollisionAgent;
import dhcoder.libgdx.collision.agent.CollisionAgent;
import dhcoder.libgdx.collision.agent.RectangleCollisionAgent;
import dhcoder.libgdx.pool.Vector2PoolBuilder;
import dhcoder.support.collection.Key2;
import dhcoder.support.math.BinarySearch;
import dhcoder.support.memory.Pool;

import java.util.HashMap;
import java.util.Map;

import static dhcoder.support.text.StringUtils.format;

public final class ShapeUtils {

    /**
     * If true, run extra sanity checks on the shapes we're testing against to make sure the inputs are always in a
     * valid state.
     */
    public static boolean RUN_SANITY_CHECKS = false;

    private static int COLLISION_SUBDIVISIONS = 8;

    private static final BinarySearch binarySearch = new BinarySearch();

    private static final Pool<Vector2> vectorPool = Vector2PoolBuilder.build(4);

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

    public static void getRepulsion(final Shape shape1, final float fromX1, final float fromY1, final float toX1,
        final float toY1, final Shape shape2, final float fromX2, final float fromY2, final float toX2,
        final float toY2, final Vector2 outRepulsion) {

        CollisionAgent agent = getCollisionAgent(shape1, shape2);

        if (RUN_SANITY_CHECKS) {
            if (agent.testIntersection(shape1, fromX1, fromY1, shape2, fromX2, fromY2)) {
                throw new IllegalStateException("getRepulsion test assumes shapes start separated.");
            }

            if (!agent.testIntersection(shape1, toX1, toY1, shape2, toX2, toY2)) {
                throw new IllegalStateException("getRepulsion test assumes shapes end collided.");
            }
        }

        // Change the frame of reference so it looks like Shape2 is standing still. This makes the math work out easier
        // later. We do this by applying shape2's velocity to shape1
        final float toX1_ = toX1 - (toX2 - fromX2);
        final float toY1_ = toY1 - (toY2 - fromY2);
        // toX2_ = fromX2; toY2_ = fromY2;

        final float deltaX1 = toX1_ - fromX1;
        final float deltaY1 = toY1_ - fromY1;
        binarySearch.initialize(COLLISION_SUBDIVISIONS);

        while (!binarySearch.isFinished()) {
            final int currentIndex = binarySearch.getCurrentIndex();
            final float percent = (float)currentIndex / COLLISION_SUBDIVISIONS;
            final float testX1 = fromX1 + deltaX1 * percent;
            final float testY1 = fromY1 + deltaY1 * percent;

            if (agent.testIntersection(shape1, testX1, testY1, shape2, fromX2, fromY2)) {
                binarySearch.rejectCurrentIndex();
            }
            else {
                binarySearch.acceptCurrentIndex();
            }
        }

        final float percent = (float)binarySearch.getAcceptedIndex() / COLLISION_SUBDIVISIONS;
        final float finalX1 = fromX1 + deltaX1 * percent;
        final float finalY1 = fromY1 + deltaY1 * percent;

        int mark = vectorPool.mark();
        Vector2 normal = vectorPool.grabNew();
        agent.getNormal(shape1, finalX1, finalY1, shape2, fromX2, fromY2, normal);
        normal.rotate90(1);
        Vector2 prevented = vectorPool.grabNew().set(toX1_, toY1_).sub(finalX1, finalY1);
        float angle = prevented.angle(normal);
        Vector2 redirected = vectorPool.grabNew();
        redirected.set(normal).scl(prevented.len() * MathUtils.cosDeg(angle));

        outRepulsion.set(redirected).sub(prevented).scl(100.0f);

        if (outRepulsion.y < 10f) {
            int breakhere = 0;
        }

//        if (true) {
//            if (agent
//                .testIntersection(shape1, toX1_ + outRepulsion.x, toY1_ + outRepulsion.y, shape2, fromX2, fromY2)) {
//                throw new IllegalStateException("getRepulsion expects shapes end separated.");
//            }
//        }

        vectorPool.freeToMark(mark);
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
