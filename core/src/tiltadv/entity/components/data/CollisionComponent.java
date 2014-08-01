package tiltadv.entity.components.data;

import dhcoder.support.collision.Collider;
import dhcoder.support.collision.CollisionSystem;
import dhcoder.support.collision.shape.Shape;
import dhcoder.support.time.Duration;
import tiltadv.Services;
import tiltadv.entity.AbstractComponent;
import tiltadv.entity.Entity;

/**
 * Component that checks if this {@link Entity} collides with any other {@link Entity} that also has a collision
 * component.
 */
public abstract class CollisionComponent extends AbstractComponent {

    private final Collider collider;
    private TransformComponent transformComponent;

    public CollisionComponent(final int groupId, final Shape shape) {
        CollisionSystem collisionSystem = Services.get(CollisionSystem.class);
        collider = collisionSystem.registerShape(groupId, shape);
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
}
