package tiltadv.components.behavior;

import dhcoder.libgdx.entity.AbstractComponent;
import dhcoder.libgdx.entity.Entity;
import tiltadv.components.box2d.BodyComponent;
import tiltadv.components.box2d.FixtureComponent;
import tiltadv.components.display.SpriteComponent;
import tiltadv.components.hierarchy.ParentComponent;

import static tiltadv.components.display.SpriteComponent.ALWAYS_BELOW;

/**
 * Component that maintains the collision logic for the main player's avatar.
 */
public final class PlayerSensorBehaviorComponent extends AbstractComponent {

    @Override
    public void initialize(final Entity owner) {
        Entity player = owner.requireComponent(ParentComponent.class).getParent();
        BodyComponent playerBody = player.requireComponent(BodyComponent.class);
        FixtureComponent sensorFixture = owner.requireComponentAfter(this, FixtureComponent.class);

        sensorFixture.setBodyComponent(playerBody);
//        offsetComponent = owner.requireComponent(OffsetComponent.class);
//
//        x = player.requireComponent(PlayerCollisionComponent.class).getShape().getHalfWidth() +
//            owner.requireComponent(PlayerSensorCollisionComponent.class).getShape().getHalfWidth();
//        x *= .8f;

        owner.requireComponent(SpriteComponent.class).setZ(ALWAYS_BELOW);
    }


    @Override
    public void reset() {
    }
}
