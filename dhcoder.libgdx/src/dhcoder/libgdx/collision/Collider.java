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

    private int groupId;
    private boolean isActive;
    private Shape shape;
    private Opt tag = Opt.withNoValue(); // Extra data associated with this collider
    private CollisionListener listener;

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
        return isActive;
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
        this.currPosition.set(currPosition);
    }

    public Vector2 getLastPosition() {
        return lastPosition;
    }

    public void updatePosition(final float x, final float y) {
        lastPosition.set(currPosition);
        currPosition.set(x, y);

        if (!isActive) {
            isActive = true; // If just activated, currPosition wasn't valid until just now
            lastPosition.set(currPosition);
        }
    }

    // Should only be called by CollisionSystem
    @Override
    public void reset() {
        shape = null;
        groupId = -1;
        isActive = false;
        listener = null;
    }

    /**
     * Initialize this collider with its shape. However, the collider won't be active until you call {@link
     * #updatePosition(float, float)} for the first time.
     */
    // Should only be called by CollisionSystem
    void initialize(final int groupId, final Shape shape, final CollisionListener listener) {
        this.groupId = groupId;
        this.shape = shape;
        isActive = false;
        this.listener = listener;
    }

    // Should only be called by CollisionSystem
    boolean collidesWith(final Collider otherCollider) {
        if (!isActive || !otherCollider.isActive || otherCollider == this) {
            return false;
        }

        return testIntersection(shape, currPosition.x, currPosition.y, otherCollider.shape,
            otherCollider.currPosition.x, otherCollider.currPosition.y);
    }

    // Should only be called by CollisionSystem
    void fireCollision(final Collision collision) {
        listener.onCollided(collision);
    }

    // Should only be called by CollisionSystem
    void fireSeparation(final Collision collision) {
        listener.onSeparated(collision);
    }

    void fireReverted(final Collision collision) {
        listener.onReverted(collision);
    }

}

