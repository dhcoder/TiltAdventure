package tiltadv.components.hierarchy.children;

import dhcoder.libgdx.entity.Entity;
import dhcoder.support.opt.Opt;
import tiltadv.components.hierarchy.ChildrenComponent;

/**
 * Class that contains a list of children entities for this {@link Entity}.
 */
public final class PlayerChildrenComponent extends ChildrenComponent {

//    private Entity sensorEntity;
    private Opt<Entity> swordEntityOpt = Opt.withNoValue();

//    public PlayerChildrenComponent setSensorEntity(final Entity sensorEntity) {
//        this.sensorEntity = sensorEntity;
//        return this;
//    }

    public PlayerChildrenComponent addSword(final Entity swordEntity) {
        swordEntityOpt.set(swordEntity);
        add(swordEntity);
        return this;
    }

    @Override
    protected void handleReset() {
//        sensorEntity = null;
        swordEntityOpt = null;
    }
}
