package tiltadv.components.hierarchy.children;

import dhcoder.libgdx.entity.Entity;
import tiltadv.components.hierarchy.ChildrenComponent;
import tiltadv.globals.EntityId;

/**
 * Class that contains a list of children entities for this {@link Entity}.
 */
public final class PlayerChildrenComponent extends ChildrenComponent {

//    private Entity sensorEntity;
    private Entity swordEntity = null;

//    public PlayerChildrenComponent setSensorEntity(final Entity sensorEntity) {
//        this.sensorEntity = sensorEntity;

    public Entity getSwordEntity() {
        return swordEntity;
    }

    @Override
    protected void handleInitialize(final Entity owner) {
        swordEntity = owner.getManager().newEntityFromTemplate(EntityId.PLAYER_SWORD);
        add(swordEntity);
    }

    @Override
    protected void handleReset() {
//        sensorEntity = null;
        swordEntity = null;
    }
}
