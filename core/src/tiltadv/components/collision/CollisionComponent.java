package tiltadv.components.collision;

import com.badlogic.gdx.math.Vector2;
import dhcoder.libgdx.collision.Collider;
import dhcoder.libgdx.collision.Collision;
import dhcoder.libgdx.collision.CollisionListener;
import dhcoder.libgdx.collision.CollisionSystem;
import dhcoder.libgdx.collision.shape.Shape;
import dhcoder.libgdx.entity.AbstractComponent;
import dhcoder.libgdx.entity.Entity;
import dhcoder.support.time.Duration;
import tiltadv.components.model.TransformComponent;
import tiltadv.globals.Services;
import tiltadv.memory.Pools;

/**
 * Component that checks if this {@link Entity} collides with any other {@link Entity} that also has a collision
 * component.
 * <p/>
 * Important: This and all subclasses depend on the {@link CollisionSystem} service being installed before being used.
 */
public abstract class CollisionComponent extends AbstractComponent implements CollisionListener {

    private final int groupId;
    private Collider collider;
    private TransformComponent transformComponent;
    private Vector2 offset = new Vector2();

    public CollisionComponent(final int groupId) {
        this.groupId = groupId;
    }

    public final CollisionComponent setShape(final Shape shape) {
        CollisionSystem collisionSystem = Services.get(CollisionSystem.class);

        collider = collisionSystem.registerShape(groupId, shape, this);

        return this;
    }

    public Collider getCollider() {
        return collider;
    }

    public Vector2 getOffset() {
        return offset;
    }

    public CollisionComponent setOffset(final Vector2 offset) {
        this.offset.set(offset);
        return this;
    }

    @Override
    public final void initialize(final Entity owner) {
        transformComponent = owner.requireComponent(TransformComponent.class);
        collider.setTag(owner);
        handleInitialize(owner);
    }

    @Override
    public final void update(final Duration elapsedTime) {
        if (collider == null) {
            return; // This can happen if this component was freed during an update loop
        }

        handleUpdate(elapsedTime);
        collider.updatePosition(transformComponent.getTranslate().x + offset.x,
            transformComponent.getTranslate().y + offset.y);
    }

    @Override
    public final void reset() {
        handleReset();

        CollisionSystem collisionSystem = Services.get(CollisionSystem.class);
        collisionSystem.release(collider);
        collider = null;

        transformComponent = null;
        offset.setZero();
    }

    @Override
    public final void onCollided(final Collision collision) { handleCollided(collision); }

    @Override
    public final void onOverlapping(final Collision collision) { handleOverlapping(collision); }

    @Override
    public final void onSeparated(final Collision collision) { handleSeparated(collision); }

    @Override
    public final void onReverted(final Collision collision) {
        int mark = Pools.vector2s.mark();
        Vector2 translate = Pools.vector2s.grabNew();
        translate.set(collider.getCurrPosition().x, collider.getCurrPosition().y);
        transformComponent.setTranslate(translate);
        Pools.vector2s.freeToMark(mark);
    }

    protected void handleInitialize(final Entity owner) {}

    protected void handleUpdate(final Duration elapsedTime) {}

    protected void handleCollided(final Collision collision) {}

    protected void handleOverlapping(final Collision collision) {}

    protected void handleSeparated(final Collision collision) {}

    protected void handleReset() {}
}
