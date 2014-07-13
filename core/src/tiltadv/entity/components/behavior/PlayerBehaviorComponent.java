package tiltadv.entity.components.behavior;

import tiltadv.entity.AbstractComponent;
import tiltadv.entity.Entity;
import tiltadv.entity.components.data.TransformComponent;

/**
 * HEADER COMMENT HERE.
 */
public class PlayerBehaviorComponent extends AbstractComponent {

    private TransformComponent transformComponent;

    @Override
    public void initialize(final Entity owner) {
        transformComponent = owner.requireComponent(TransformComponent.class);
    }

    @Override
    public void update(final float elapsedTime) {
        transformComponent.rotation.setDegrees(transformComponent.rotation.getDegrees() + 5f);
    }
}
