package dhcoder.libgdx.collision;

import com.badlogic.gdx.math.Vector2;
import dhcoder.libgdx.collision.shape.Shape;
import dhcoder.support.collection.ArrayMap;
import dhcoder.support.memory.Pool;

import java.util.ArrayList;

import static dhcoder.libgdx.collision.shape.ShapeUtils.getIntersection;
import static dhcoder.support.collection.ListUtils.swapToEndAndRemove;
import static dhcoder.support.text.StringUtils.format;

/**
 * Class which manages a collection of shapes and reports back when any of them overlap.
 * <p/>
 * To use this class, first register a bunch of shapes by calling {@link #registerShape(int,
 * Shape)}. Each shape is associated with a group ID, which must be a bitmask value (1, 2, 4, 8,
 * etc.). You must also specify which groups can collides with which via {@link #registerCollidesWith(int,
 * int)} after creating it, or else nothing will collide with anything.
 * <p/>
 * Each registration method returns a {@link Collider} which you use to listen for collisions (by adding a
 * listener to {@link Collider#onCollided} and to update the position of the shapes.
 * <p/>
 * Finally, call {@link #triggerCollisions()} to cause the manager to run through all items and fire the events for
 * any that collided.
 */
public final class CollisionSystem {

    private static final int NUM_GROUPS = 32; // One group per integer bit, so 32 bits means 32 groups.

    private final Pool<Collider> colliderPool;
    private final Pool<Collision> collisionPool;
    private final Pool<ColliderKey> colliderKeyPool = Pool.of(ColliderKey.class, 1);
    private final Pool<Intersection> intersectionPool = Pool.of(Intersection.class, 1);
    private final Pool<Vector2> vectorPool = new Pool<Vector2>(new Pool.AllocateMethod<Vector2>() {
        @Override
        public Vector2 run() {
            return new Vector2();
        }
    }, new Pool.ResetMethod<Vector2>() {
        @Override
        public void run(final Vector2 item) {
            item.setZero();
        }
    }, 4);

    private final int[] collidesWith; // group -> bitmask of groups it collides with
    private final ArrayList<ArrayList<Collider>> groups;

    private final ArrayMap<ColliderKey, Collision> collisions;

    public CollisionSystem(final int colliderCapacity) {
        colliderPool = Pool.of(Collider.class, colliderCapacity);
        // Given n colliders, there can be up to n² collisionPool. In practice, however, there are much less. Allocate a
        // subset of n² to save memory but we may adjust this when we see the number of collisionPool in the wild.
        int collisionCapacity = colliderCapacity * 2;
        collisionPool = Pool.of(Collision.class, collisionCapacity);
        collisions = new ArrayMap<ColliderKey, Collision>(collisionCapacity);

        collidesWith = new int[NUM_GROUPS];
        groups = new ArrayList<ArrayList<Collider>>(NUM_GROUPS);
        for (int i = 0; i < NUM_GROUPS; ++i) {
            groups.add(new ArrayList<Collider>(colliderCapacity));
        }
    }

    public void registerCollidesWith(final int groupId, final int collidesWithMask) {
        requireValidGroupId(groupId);

        int groupIdToIndex = groupIdToIndex(groupId);
        collidesWith[groupIdToIndex] = collidesWithMask;
    }

    public Collider registerShape(final int groupId, final Shape shape) {
        requireValidGroupId(groupId);
        int groupIndex = groupIdToIndex(groupId);

        Collider collider = colliderPool.grabNew();
        collider.initialize(groupId, shape);
        groups.get(groupIndex).add(collider);

        return collider;
    }

    public void triggerCollisions() {
        ColliderKey key = colliderKeyPool.grabNew();
        for (int groupSourceIndex = 0; groupSourceIndex < NUM_GROUPS; ++groupSourceIndex) {
            ArrayList<Collider> groupSource = groups.get(groupSourceIndex);
            int groupSourceSize = groupSource.size();
            for (int groupTargetIndex = 0; groupTargetIndex < NUM_GROUPS; ++groupTargetIndex) {
                if (groupsCanCollide(groupSourceIndex, groupTargetIndex)) {
                    ArrayList<Collider> groupTarget = groups.get(groupTargetIndex);
                    int groupTargetSize = groupTarget.size();
                    for (int colliderSourceIndex = 0; colliderSourceIndex < groupSourceSize; ++colliderSourceIndex) {
                        Collider colliderSource = groupSource.get(colliderSourceIndex);
                        for (int colliderTargetIndex = 0; colliderTargetIndex < groupTargetSize;
                             ++colliderTargetIndex) {
                            Collider colliderTarget = groupTarget.get(colliderTargetIndex);
                            key.set(colliderSource, colliderTarget);
                            if (colliderSource.collidesWith(colliderTarget)) {
                                Collision collision;
                                if (!collisions.containsKey(key)) {
                                    collision = collisionPool.grabNew();
                                    collision.set(colliderSource, colliderTarget);
                                    collisions.put(collision.getKey(), collision);
                                    boolean debugTest = collisions.containsKey(key);
                                    colliderSource.fireCollision(collision);
                                }
                                else {
                                    // collision = collisions.get(key);
                                    // Report continued collision?
                                }
                            }
                            else if (collisions.containsKey(key)) {
                                Collision collision = collisions.remove(key);
                                colliderSource.fireSeparation(collision);
                                collisionPool.free(collision);
                            }
                        }
                    }
                }
            }
        }
        colliderKeyPool.free(key);
    }

    public void release(final Collider collider) {
        removeFromGroup(collider);
        colliderPool.free(collider);
    }

    /**
     * Given a collision we want to revert, change the location of the source collider to a new destination that avoids
     * the target collider (by sliding alongside it)
     */
    public void redirectSourceToAvoidCollision(final Collision collision) {

        Intersection intersection = intersectionPool.grabNew();
        Collider source = collision.getSource();
        Collider target = collision.getTarget();
        getIntersection(source.getShape(), source.getLastX(), source.getLastY(), source.getCurrX(), source.getCurrY(),
            target.getShape(), target.getLastX(), target.getLastY(), target.getCurrX(), target.getCurrY(),
            intersection);

        Vector2 finalPosition = vectorPool.grabNew();
        finalPosition.set(intersection.getSourcePosition());
        finalPosition.add(intersection.getNormalForce());
        source.fixCurrentPosition(finalPosition.x, finalPosition.y);
        vectorPool.free(finalPosition);

        intersectionPool.free(intersection);

        // This collision never happened... remove it quietly...
        collisions.remove(collision.getKey());
        collisionPool.free(collision);
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

    private boolean groupsCanCollide(final int sourceGroupIndex, final int targetGroupIndex) {
        int collidesWithMask = collidesWith[sourceGroupIndex];
        int targetGroupMask = 1 << targetGroupIndex;

        return (collidesWithMask & targetGroupMask) != 0;
    }
}
