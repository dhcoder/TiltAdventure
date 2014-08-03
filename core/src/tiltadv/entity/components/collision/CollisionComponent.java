package tiltadv.entity.components.collision;

import dhcoder.support.collision.Collider;
import dhcoder.support.collision.Collision;
import dhcoder.support.collision.CollisionEventArgs;
import dhcoder.support.collision.CollisionSystem;
import dhcoder.support.collision.shape.Shape;
import dhcoder.support.event.ArgEventListener;
import dhcoder.support.time.Duration;
import tiltadv.Services;
import tiltadv.entity.AbstractComponent;
import tiltadv.entity.Entity;
import tiltadv.entity.components.model.TransformComponent;

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

    protected void handleCollided(final Collision collision) {}

    protected void handleSeparated(final Collision collision) {}
}
