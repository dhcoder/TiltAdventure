package dhcoder.libgdx.collision;

import com.badlogic.gdx.math.Vector2;
import dhcoder.libgdx.collision.shape.Shape;
import dhcoder.support.memory.Poolable;
import dhcoder.support.opt.Opt;

import static dhcoder.libgdx.collision.shape.ShapeUtils.testIntersection;

/**
 * A class that encapsulates a shape and position used for collision testing.
 */
public final class Collider implements Poolable {

    private final Vector2 lastPosition = new Vector2();
    private final Vector2 currPosition = new Vector2();

    private CollisionSystem system;
    private int groupId;
    private boolean isInitialized;
    private boolean isEnabled;
    private Shape shape;
    private Opt tag = Opt.withNoValue(); // Extra data associated with this collider
    private CollisionListener listener;

    /**
     * Restricted access - use {@link CollisionSystem#registerShape(int, Shape, CollisionListener)} to create a
     * collider.
     */
    Collider() { reset(); }

    /**
     * Returns optional data associated with this collider.
     */
    public Opt getTag() {
        return tag;
    }

    public void setTag(final Object tag) {
        this.tag.set(tag);
    }

    public boolean isActive() {
        return isInitialized && isEnabled;
    }

    public void setEnabled(final boolean isEnabled) {
        this.isEnabled = isEnabled;
    }

    public Shape getShape() {
        return shape;
    }

    /**
     * Returns the ID of the group this collider belongs in.
     */
    public int getGroupId() {
        return groupId;
    }

    public Vector2 getCurrPosition() {
        return currPosition;
    }

    // Should only be called by Collision
    // Used to rewrite history, telling the collider that it didn't really go to where it thought it did...
    // This is useful, for example, to pop a shape out after it penetrated an object that should be solid.
    void setCurrPosition(final Vector2 currPosition) {
        system.removeColliderFromRegions(this);
        this.currPosition.set(currPosition);
        system.addColliderIntoRegions(this);
    }

    public Vector2 getLastPosition() {
        return lastPosition;
    }

    public void updatePosition(final float x, final float y) {
        if (isInitialized) {
            if (lastPosition.epsilonEquals(x, y, 0f)) {
                return;
            }
            system.removeColliderFromRegions(this);
            lastPosition.set(currPosition);
            currPosition.set(x, y);
            system.addColliderIntoRegions(this);
        }
        if (!isInitialized) {
            isInitialized = true;
            currPosition.set(x, y);
            lastPosition.set(currPosition);
            system.addColliderIntoRegions(this);
        }
    }

    // Should only be called by CollisionSystem
    @Override
    public void reset() {
        system = null;
        shape = null;
        groupId = -1;
        isInitialized = false;
        isEnabled = true;
        listener = null;
    }

    /**
     * Initialize this collider with its shape. However, the collider won't be active until you call {@link
     * #updatePosition(float, float)} for the first time.
     */
    // Should only be called by CollisionSystem
    void set(final CollisionSystem system, final int groupId, final Shape shape, final CollisionListener listener) {
        this.system = system;
        this.groupId = groupId;
        this.shape = shape;
        isInitialized = false;
        this.listener = listener;
    }

    // Should only be called by CollisionSystem
    boolean collidesWith(final Collider otherCollider) {
        if (!isActive() || !otherCollider.isActive() || otherCollider == this) {
            return false;
        }

        return testIntersection(shape, currPosition.x, currPosition.y, otherCollider.shape,
            otherCollider.currPosition.x, otherCollider.currPosition.y);
    }

    // Should only be called by CollisionSystem
    void fireCollision(final Collision collision) {
        listener.onCollided(collision);
    }

    void fireOverlapping(final Collision collision) {
        listener.onOverlapping(collision);
    }

    // Should only be called by CollisionSystem
    void fireSeparation(final Collision collision) {
        listener.onSeparated(collision);
    }

    // Should only be called by CollisionSystem
    void fireReverted(final Collision collision) {
        listener.onReverted(collision);
    }

}

