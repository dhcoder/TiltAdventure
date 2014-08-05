package tiltadv.components.collision;

import com.badlogic.gdx.math.Vector2;
import dhcoder.libgdx.collision.Collider;
import dhcoder.libgdx.collision.Collision;
import dhcoder.libgdx.collision.CollisionEventArgs;
import dhcoder.libgdx.collision.CollisionSystem;
import dhcoder.libgdx.collision.shape.Shape;
import dhcoder.libgdx.entity.AbstractComponent;
import dhcoder.libgdx.entity.Entity;
import dhcoder.support.event.ArgEventListener;
import dhcoder.support.time.Duration;
import tiltadv.Services;
import tiltadv.components.model.TransformComponent;
import tiltadv.memory.Pools;

/**
 * Component that checks if this {@link Entity} collides with any other {@link Entity} that also has a collision
 * component.
 * <p/>
 * Important: This and all subclasses depend on the {@link CollisionSystem} service being installed before being used.
 */
public abstract class CollisionComponent extends AbstractComponent {

    private final Collider collider;
    private TransformComponent transformComponent;

    public CollisionComponent(final int groupId, final Shape shape) {
        CollisionSystem collisionSystem = Services.get(CollisionSystem.class);
        collider = collisionSystem.registerShape(groupId, shape);
        collider.onCollided.addListener(new ArgEventListener<CollisionEventArgs>() {
            @Override
            public void run(final Object sender, final CollisionEventArgs args) {
                handleCollided(args.getCollision());
            }
        });
        collider.onSeparated.addListener(new ArgEventListener<CollisionEventArgs>() {
            @Override
            public void run(final Object sender, final CollisionEventArgs args) {
                handleSeparated(args.getCollision());
            }
        });
    }

    @Override
    public final void initialize(final Entity owner) {
        transformComponent = owner.requireComponent(TransformComponent.class);
    }

    @Override
    public final void update(final Duration elapsedTime) {
        collider.updatePosition(transformComponent.getTranslate().x, transformComponent.getTranslate().y);
    }

    @Override
    public final void dispose() {
        CollisionSystem collisionSystem = Services.get(CollisionSystem.class);
        collisionSystem.release(collider);
    }

    /**
     * Sync the current entity's location with what the collision system says it should be.
     * <p/>
     * Normally, the collision system simply mirrors the location of an entity. But occasionally, it talks back,
     * saying that an entity shouldn't have moved where it did. In this case, we need to communicate that information
     * back to the entity.
     * <p/>
     * Subclasses should call this method any time our collider has changed in a way that the associated entity should
     * conform to.
     */
    protected final void syncEntityWithCollisionSystem() {
        Vector2 translate = Pools.vectors.grabNew();
        translate.set(collider.getCurrX(), collider.getCurrY());
        transformComponent.setTranslate(translate);
        Pools.vectors.free(translate);
    }

    protected void handleCollided(final Collision collision) {}

    protected void handleSeparated(final Collision collision) {}
}
