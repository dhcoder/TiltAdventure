package dhcoder.libgdx.collision;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import dhcoder.libgdx.collision.shape.Shape;
import dhcoder.libgdx.collision.shape.ShapeUtils;
import dhcoder.libgdx.pool.Vector2PoolBuilder;
import dhcoder.support.collection.ArrayMap;
import dhcoder.support.collection.ArraySet;
import dhcoder.support.math.IntCoord;
import dhcoder.support.memory.Pool;
import dhcoder.support.opt.Opt;

import java.util.ArrayList;
import java.util.List;

import static dhcoder.support.collection.ListUtils.swapToEndAndRemove;
import static dhcoder.support.text.StringUtils.format;

/**
 * Class which manages a collection of shapes and reports back when any of them overlap.
 * <p/>
 * To use this class, first register a bunch of shapes by calling {@link #registerShape(int, Shape,
 * CollisionListener)}. When a collision happens, the listener passed into this method will be notified.
 * <p/>
 * Each shape is associated with a group ID, which must be a bitmask value (1, 2, 4, 8,
 * etc.). Use {@link #registerCollidesWith(int, int)} when creating a collision system to specify which groups can
 * collide with one another.
 * <p/>
 * Finally, call {@link #triggerCollisions()} to cause the manager to run through all items and fire the events for
 * any that collided.
 */
public final class CollisionSystem {

    /**
     * If true, run extra sanity checks on the shapes we're testing against to make sure the inputs are always in a
     * valid state.
     */
    public static boolean RUN_SANITY_CHECKS = false;
    public static final int REGION_MARGIN = 1;
    private static final int NUM_GROUPS = 32; // One group per integer bit, so 32 bits means 32 groups.
    // Divide the screen into NxN rectangles, and only do collision checks within those regions.
    private static final int REGION_SIZE = 40;
    private static final int HALF_REGION_SIZE = REGION_SIZE / 2;

    private final Pool<Collider> colliderPool;
    private final Pool<Collision> collisionPool;
    private final Pool<Vector2> vectorPool = Vector2PoolBuilder.build(2);
    private final Pool<CollisionKey> colliderKeyPool = Pool.of(CollisionKey.class, 1);
    private final Pool<CollisionRegion> regionPool = Pool.of(CollisionRegion.class, 20).makeResizable(200);
    private final Pool<IntCoord> intCoordPool = Pool.of(IntCoord.class, 3);
    private final Pool<Opt> optPool = Pool.of(Opt.class, 1);

    private final int[] collidesWith; // group -> bitmask of groups it collides with
    private final ArrayList<ArrayList<Collider>> groups;

    private final ArrayMap<CollisionKey, Collision> collisions;
    private final ArraySet<CollisionKey> collidedThisFrame;
    private final ArrayMap<IntCoord, CollisionRegion> regionMap;

    public CollisionSystem(final int colliderCapacity) {
        colliderPool = Pool.of(Collider.class, colliderCapacity);
        // Given n colliders, there can be up to n² collisionPool. In practice, however, there are much less. Allocate a
        // subset of n² to save memory but we may adjust this when we see the number of collisionPool in the wild.
        int collisionCapacity = colliderCapacity * 2;
        collisionPool = Pool.of(Collision.class, collisionCapacity);
        collisions = new ArrayMap<CollisionKey, Collision>(collisionCapacity);
        collidedThisFrame = new ArraySet<CollisionKey>(20);

        collidesWith = new int[NUM_GROUPS];
        groups = new ArrayList<ArrayList<Collider>>(NUM_GROUPS);
        for (int i = 0; i < NUM_GROUPS; ++i) {
            groups.add(new ArrayList<Collider>(colliderCapacity));
        }
        regionMap = new ArrayMap<IntCoord, CollisionRegion>();
    }

    public void registerCollidesWith(final int groupId, final int collidesWithMask) {
        requireValidGroupId(groupId);

        int groupIdToIndex = groupIdToIndex(groupId);
        collidesWith[groupIdToIndex] = collidesWithMask;
    }

    public Collider registerShape(final int groupId, final Shape shape, final CollisionListener listener) {
        requireValidGroupId(groupId);
        int groupIndex = groupIdToIndex(groupId);

        Collider collider = colliderPool.grabNew();
        collider.set(this, groupId, shape, listener);
        groups.get(groupIndex).add(collider);

        return collider;
    }

    public void triggerCollisions() {
        collidedThisFrame.clear();

        List<CollisionRegion> regions = regionPool.getItemsInUse();
        int numRegions = regions.size();
        for (int i = 0; i < numRegions; i++) {
            CollisionRegion region = regions.get(i);
            region.triggerCollisions();
        }

        // Clean up any regions that are now empty after resolving collisions
        for (int i = 0; i < numRegions; i++) {
            CollisionRegion region = regions.get(i);
            if (region.isEmpty()) {
                regionMap.remove(region.getCoordinates());
                regionPool.free(region);
                --i;
                --numRegions;
            }
        }

        // Clean up any collisions that are pointing to a collider that was freed after resolving collisions
        final List<Collision> collisions = collisionPool.getItemsInUse();
        int numCollisions = collisions.size();
        for (int i = 0; i < numCollisions; i++) {
            Collision collision = collisions.get(i);
            if (!collision.getSource().isActive() || !collision.getTarget().isActive()) {
                freeCollision(collision);
                --i;
                --numCollisions;
            }
        }
    }

    public void release(final Collider collider) {
        removeColliderFromRegions(collider);
        removeFromGroup(collider);
        colliderPool.free(collider);
    }

    /**
     * Given a collision that *just* happened, change the location of the source collider to a new destination so that it no longer
     * penetrates the target collider.
     */
    public void extractSourceCollider(final Collision collision) {
        Vector2 repulsion = vectorPool.grabNew();

        Collider source = collision.getSource();
        Collider target = collision.getTarget();
        Vector2 sourceCurrPos = source.getCurrPosition();
        Vector2 sourceLastPos = source.getLastPosition();
        Vector2 targetCurrPos = target.getCurrPosition();
        Vector2 targetLastPos = target.getLastPosition();
        ShapeUtils.getRepulsion(source.getShape(), sourceLastPos.x, sourceLastPos.y, sourceCurrPos.x, sourceCurrPos.y,
            target.getShape(), targetLastPos.x, targetLastPos.y, targetCurrPos.x, targetCurrPos.y, repulsion);

        Vector2 finalPosition = vectorPool.grabNew();
        finalPosition.set(source.getCurrPosition()).add(repulsion);
        source.setCurrPosition(finalPosition);

        vectorPool.free(finalPosition);
        vectorPool.free(repulsion);

        // This collision never happened... remove it quietly...
        source.fireReverted(collision);
        freeCollision(collision);
    }

    public void render(final ShapeRenderer renderer) {
        final List<Collider> colliders = colliderPool.getItemsInUse();
        int numColliders = colliders.size();
        for (int i = 0; i < numColliders; i++) {
            Collider collider = colliders.get(i);
            if (!collider.isActive()) {
                continue;
            }

            Vector2 pos = collider.getCurrPosition();
            collider.getShape().render(renderer, pos.x, pos.y);
        }

//        final List<CollisionRegion> regions = regionPool.getItemsInUse();
//        int numRegions = regions.size();
//        for (int i = 0; i < numRegions; i++) {
//            CollisionRegion region = regions.get(i);
//            final IntCoord coords = region.getCoordinates();
//            float x = (coords.getX() * REGION_SIZE) - REGION_SIZE / 2f;
//            float y = (coords.getY() * REGION_SIZE) - REGION_SIZE / 2f;
//            renderer.rect(x + REGION_MARGIN, y + REGION_MARGIN, REGION_SIZE - REGION_MARGIN * 2,
//                REGION_SIZE - REGION_MARGIN * 2);
//        }
    }

    void addColliderIntoRegions(final Collider collider) {
        int mark = intCoordPool.mark();

        IntCoord topLeft = intCoordPool.grabNew();
        IntCoord bottomRight = intCoordPool.grabNew();
        getColliderBounds(collider, topLeft, bottomRight);
        IntCoord currCoord = intCoordPool.grabNew();
        for (int x = topLeft.getX(); x <= bottomRight.getX(); ++x) {
            for (int y = topLeft.getY(); y >= bottomRight.getY(); --y) {
                currCoord.set(x, y);
                CollisionRegion region;
                if (!regionMap.containsKey(currCoord)) {
                    region = regionPool.grabNew().setCollisionSystem(this).setCoordinates(currCoord);
                    regionMap.put(region.getCoordinates(), region);
                }
                else {
                    region = regionMap.get(currCoord);
                }
                region.addCollider(collider);
            }
        }

        intCoordPool.freeToMark(mark);
    }

    void removeColliderFromRegions(final Collider collider) {
        int mark = intCoordPool.mark();

        IntCoord topLeft = intCoordPool.grabNew();
        IntCoord bottomRight = intCoordPool.grabNew();
        getColliderBounds(collider, topLeft, bottomRight);
        IntCoord currCoord = intCoordPool.grabNew();
        for (int x = topLeft.getX(); x <= bottomRight.getX(); ++x) {
            for (int y = topLeft.getY(); y >= bottomRight.getY(); --y) {
                currCoord.set(x, y);
                CollisionRegion region = regionMap.get(currCoord);
                region.remove(collider);
            }
        }

        intCoordPool.freeToMark(mark);
    }

    void testForCollision(final Collider sourceCollider, final Collider targetCollider) {
        if (!groupsCanCollide(sourceCollider.getGroupId(), targetCollider.getGroupId())) {
            return;
        }

        CollisionKey key = colliderKeyPool.grabNew();
        key.set(sourceCollider, targetCollider);

        if (sourceCollider.collidesWith(targetCollider)) {
            if (collidedThisFrame.contains(key)) {
                // We already tested this exact collision this loop. This can sometimes happen when two objects both
                // stretch over multiple collision regions, so we see their collision twice. Ignore this one!
                colliderKeyPool.free(key);
                return;
            }

            if (!collisions.containsKey(key)) {
                Collision collision = collisionPool.grabNew();
                collision.set(sourceCollider, targetCollider);
                collisions.put(collision.getKey(), collision);

                collidedThisFrame.put(collision.getKey());
                sourceCollider.fireCollision(collision);
            }
            else {
                Collision collision = collisions.get(key);

                collidedThisFrame.put(collision.getKey());
                sourceCollider.fireOverlapping(collision);
            }
        }
        else if (collisions.containsKey(key)) {
            Collision collision = collisions.remove(key);
            collidedThisFrame.removeIf(key);
            sourceCollider.fireSeparation(collision);
            collisionPool.free(collision);
        }
        colliderKeyPool.free(key);
    }

    private void freeCollision(final Collision collision) {
        collidedThisFrame.removeIf(collision.getKey());
        collisions.remove(collision.getKey());
        collisionPool.free(collision);
    }

    private void getColliderBounds(final Collider collider, final IntCoord outTopLeft, final IntCoord outBottomRight) {
        Vector2 origin = collider.getCurrPosition();
        Shape shape = collider.getShape();
        float left = shape.getLeft(origin.x);
        float right = shape.getRight(origin.x);
        float top = shape.getTop(origin.y);
        float bottom = shape.getBottom(origin.y);
        int leftCoord = (int)((Math.abs(left) + HALF_REGION_SIZE) / REGION_SIZE);
        int rightCoord = (int)((Math.abs(right) + HALF_REGION_SIZE) / REGION_SIZE);
        int topCoord = (int)((Math.abs(top) + HALF_REGION_SIZE) / REGION_SIZE);
        int bottomCoord = (int)((Math.abs(bottom) + HALF_REGION_SIZE) / REGION_SIZE);
        if (left < 0) {
            leftCoord = -leftCoord;
        }
        if (right < 0) {
            rightCoord = -rightCoord;
        }
        if (top < 0) {
            topCoord = -topCoord;
        }
        if (bottom < 0) {
            bottomCoord = -bottomCoord;
        }

        outTopLeft.set(leftCoord, topCoord);
        outBottomRight.set(rightCoord, bottomCoord);
    }

    private boolean groupsCanCollide(final int sourceGroupId, final int targetGroupId) {
        final int sourceGroupIndex = groupIdToIndex(sourceGroupId);
        int collidesWithMask = collidesWith[sourceGroupIndex];
        return (collidesWithMask & targetGroupId) != 0;
    }

    private void requireValidGroupId(final int group) {
        // Here, we're testing if 'group' only has a single bit set
        // See also: http://stackoverflow.com/a/3160716/1299302
        if ((group & (group - 1)) != 0) {
            throw new IllegalArgumentException(format("Invalid group ID {0}, should be a power of 2", group));
        }
    }

    private void removeFromGroup(final Collider collider) {
        int groupIndex = groupIdToIndex(collider.getGroupId());
        ArrayList<Collider> group = groups.get(groupIndex);
        swapToEndAndRemove(group, collider);
    }

    private int groupIdToIndex(final int groupId) {
        int groupIdCopy = groupId;
        int index = 0;

        while ((groupIdCopy >>= 1) > 0) {
            index++;
        }
        return index;
    }
}
