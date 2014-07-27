package dhcoder.support.collision;

import dhcoder.support.collision.shape.Circle;
import dhcoder.support.collision.shape.Rectangle;
import dhcoder.support.collision.shape.Shape;
import dhcoder.support.memory.Pool;

import java.util.ArrayList;

import static dhcoder.support.utils.ListUtils.swapToEndAndRemove;
import static dhcoder.support.utils.ShapeUtils.testIntersection;
import static dhcoder.support.utils.StringUtils.format;

/**
 * Class which manages a collection of shapes and reports back when any of them overlap.
 * <p/>
 * To use this class, first register a bunch of shapes by calling {@link #registerCircle(int, float, float,
 * float)} and {@link #registerRectangle(int, float, float, float, float)}. Each shape is associated with a group ID,
 * which must be a bitmask value (1, 2, 4, 8, etc.). You must also specify which groups can collides with which via
 * {@link #registerCollidesWith(int, int)} after creating it, or else nothing will collide with anything.
 * <p/>
 * Each registration method returns a {@link CollisionHandle} which you use to listen for collisions (by adding a
 * listener to {@link CollisionHandle#onCollision} and to update the position of the shapes.
 * <p/>
 * Finally, call {@link #triggerCollisions()} to cause the manager to run through all items and fire the events for
 * any that collided.
 */
public final class CollisionManager {

    private static final int NUM_GROUPS = 32; // One group per integer bit, so 32 bits means 32 groups.

    private final Pool<CollisionHandle> handles;
    private final Pool<Circle> circles;
    private final Pool<Rectangle> rectangles;

    private final int[] collidesWith; // group -> bitmask of groups it collides with
    private final ArrayList<ArrayList<CollisionHandle>> groups;

    public CollisionManager(final int maxCapacity) {
        handles = new Pool<CollisionHandle>(new Pool.AllocateMethod<CollisionHandle>() {
            @Override
            public CollisionHandle run() {
                return new CollisionHandle();
            }
        }, new Pool.ResetMethod<CollisionHandle>() {
            @Override
            public void run(final CollisionHandle item) {
                item.reset();
            }
        }, maxCapacity);

        circles = new Pool<Circle>(new Pool.AllocateMethod<Circle>() {
            @Override
            public Circle run() {
                return new Circle();
            }
        }, new Pool.ResetMethod<Circle>() {
            @Override
            public void run(final Circle item) {
                item.reset();
            }
        }, maxCapacity);

        rectangles = new Pool<Rectangle>(new Pool.AllocateMethod<Rectangle>() {
            @Override
            public Rectangle run() {
                return new Rectangle();
            }
        }, new Pool.ResetMethod<Rectangle>() {
            @Override
            public void run(final Rectangle item) {
                item.reset();
            }
        }, maxCapacity);

        collidesWith = new int[NUM_GROUPS];
        groups = new ArrayList<ArrayList<CollisionHandle>>(NUM_GROUPS);
        for (int i = 0; i < NUM_GROUPS; ++i) {
            groups.add(new ArrayList<CollisionHandle>(maxCapacity));
        }
    }

    public void registerCollidesWith(final int groupId, final int collidesWithMask) {
        requireValidGroupId(groupId);

        int groupIdToIndex = groupIdToIndex(groupId);
        collidesWith[groupIdToIndex] = collidesWithMask;
    }

    public CollisionHandle registerCircle(final int groupId, final float x, final float y, final float radius) {
        requireValidGroupId(groupId);

        Circle circle = circles.grabNew();
        circle.setOrigin(x, y);
        circle.setRadius(radius);

        CollisionHandle handle = handles.grabNew();
        handle.setShape(circle);

        addToGroup(groupId, handle);

        return handle;
    }

    public CollisionHandle registerRectangle(final int groupId, final float x, final float y, final float halfWidth,
        final float halfHeight) {
        requireValidGroupId(groupId);

        Rectangle rectangle = rectangles.grabNew();
        rectangle.setOrigin(x, y);
        rectangle.setHalfSize(halfWidth, halfHeight);

        CollisionHandle handle = handles.grabNew();
        handle.setShape(rectangle);
        addToGroup(groupId, handle);

        return handle;
    }

    public void triggerCollisions() {
        for (int groupSourceIndex = 0; groupSourceIndex < NUM_GROUPS; ++groupSourceIndex) {
            ArrayList<CollisionHandle> groupSource = groups.get(groupSourceIndex);
            int groupSourceSize = groupSource.size();
            for (int groupTargetIndex = 0; groupTargetIndex < NUM_GROUPS; ++groupTargetIndex) {
                if (groupsCanCollide(groupSourceIndex, groupTargetIndex)) {
                    ArrayList<CollisionHandle> groupTarget = groups.get(groupTargetIndex);
                    int groupTargetSize = groupTarget.size();
                    for (int handleSourceIndex = 0; handleSourceIndex < groupSourceSize; ++handleSourceIndex) {
                        CollisionHandle handleSource = groupSource.get(handleSourceIndex);
                        for (int handleTargetIndex = 0; handleTargetIndex < groupTargetSize; ++handleTargetIndex) {
                            CollisionHandle handleTarget = groupTarget.get(handleTargetIndex);
                            if (handleSource == handleTarget) { continue; }
                            if (testIntersection(handleSource.getShape(), handleTarget.getShape())) {
                                handleSource.fireCollision(handleTarget);
                            }
                        }
                    }
                }
            }
        }
    }

    public void release(final CollisionHandle handle) {
        Shape shape = handle.getShape();
        if (shape instanceof Circle) {
            circles.free((Circle)shape);
        }
        else if (shape instanceof Rectangle) {
            rectangles.free((Rectangle)shape);
        }
        else {
            throw new UnsupportedOperationException(format("Unexpected shape: {0}", shape.getClass()));
        }
        removeFromGroup(handle);
        handles.free(handle);
    }

    private void requireValidGroupId(final int group) {
        // Here, we're testing if 'group' only has a single bit set
        // See also: http://stackoverflow.com/a/3160716/1299302
        if ((group & (group - 1)) != 0) {
            throw new IllegalArgumentException(format("Invalid group ID {0}, should be a power of 2", group));
        }
    }

    private void addToGroup(final int groupId, final CollisionHandle handle) {
        int groupIndex = groupIdToIndex(groupId);
        handle.setGroupIndex(groupIndex);
        groups.get(groupIndex).add(handle);
    }

    private void removeFromGroup(final CollisionHandle handle) {
        ArrayList<CollisionHandle> group = groups.get(handle.getGroupIndex());
        swapToEndAndRemove(group, handle);
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
