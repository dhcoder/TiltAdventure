package tiltadv.entity.components.data;

import dhcoder.support.collision.CollisionHandle;
import dhcoder.support.collision.CollisionManager;
import dhcoder.support.collision.shape.Shape;
import dhcoder.support.time.Duration;
import tiltadv.Services;
import tiltadv.entity.AbstractComponent;
import tiltadv.entity.Entity;
import tiltadv.constants.Group;

/**
 * Component that checks if this {@link Entity} collides with any other {@link Entity} that also has a collision
 * component.
 */
public final class CollisionComponent extends AbstractComponent {

    private TransformComponent transformComponent;
    private CollisionHandle collisionHandle;

    public CollisionComponent(final int groupId, final Shape shape) {
        CollisionManager collisionManager = Services.get(CollisionManager.class);
        collisionHandle = collisionManager.registerShape(groupId, shape);
    }

    @Override
    public void initialize(final Entity owner) {
        transformComponent = owner.requireComponent(TransformComponent.class);
    }

    @Override
    public void update(final Duration elapsedTime) {
        collisionHandle.updateOrigin(transformComponent.getTranslate().x, transformComponent.getTranslate().y);
    }

    @Override
    public void dispose() {
        CollisionManager collisionManager = Services.get(CollisionManager.class);
        collisionManager.release(collisionHandle);
    }
}
