package tiltadv.components.input.touchables;

import dhcoder.libgdx.entity.Entity;
import tiltadv.components.input.TouchableComponent;
import tiltadv.globals.Events;

/**
 * Any entity that can be selected and thereby targetted.
 */
public final class TargetTouchableComponent extends TouchableComponent {

    private Entity owner;

    @Override
    protected void handleInitialized(final Entity owner) {
        this.owner = owner;
    }

    @Override
    public void handleDeselected() {
        Events.onTargetCleared.fire(owner);
    }

    @Override
    protected void handleReset() {
        owner = null;
    }

    @Override
    protected void handleSelected() {
        Events.onTargetSelected.fire(owner);
    }


}
