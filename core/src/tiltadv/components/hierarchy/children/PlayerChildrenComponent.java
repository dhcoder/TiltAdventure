package tiltadv.components.hierarchy.children;

import dhcoder.libgdx.entity.Entity;
import tiltadv.components.dynamics.box2d.BodyComponent;
import tiltadv.components.dynamics.box2d.OffsetComponent;
import tiltadv.components.hierarchy.ChildrenComponent;
import tiltadv.globals.EntityId;

/**
 * Class that contains a list of children entities for this {@link Entity}.
 */
public final class PlayerChildrenComponent extends ChildrenComponent {

    private Entity sensorEntity;
    private Entity swordEntity;

    public Entity getSwordEntity() {
        return swordEntity;
    }

    @Override
    protected void handleInitialize(final Entity owner) {
//        swordEntity = owner.getManager().newEntityFromTemplate(EntityId.PLAYER_SWORD);
//        final BodyComponent bodyComponent = owner.requireComponentBefore(this, BodyComponent.class);
//        swordEntity.requireComponent(RevoluteJointComponent.class).setTargetBody(bodyComponent.getBody());
//        add(swordEntity);

        final BodyComponent bodyComponent = owner.requireComponentBefore(this, BodyComponent.class);
        sensorEntity = owner.getManager().newEntityFromTemplate(EntityId.PLAYER_SENSOR);
        sensorEntity.requireComponent(OffsetComponent.class).setTargetBody(bodyComponent.getBody());
        add(sensorEntity);
    }

    @Override
    protected void handleReset() {
        sensorEntity = null;
        swordEntity = null;
    }
}
