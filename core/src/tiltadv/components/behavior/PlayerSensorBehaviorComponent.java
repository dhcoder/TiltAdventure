package tiltadv.components.behavior;

import dhcoder.libgdx.entity.AbstractComponent;
import dhcoder.libgdx.entity.Entity;
import tiltadv.components.display.SpriteComponent;

import static tiltadv.components.display.SpriteComponent.ALWAYS_BELOW;

/**
 * Component that maintains the collision logic for the main player's avatar.
 */
public final class PlayerSensorBehaviorComponent extends AbstractComponent {

    @Override
    public void initialize(final Entity owner) {
        owner.requireComponent(SpriteComponent.class).setZ(ALWAYS_BELOW);
    }


    @Override
    public void reset() {
    }
}
