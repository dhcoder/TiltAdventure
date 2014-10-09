package tiltadv.components.input.touchables;

import dhcoder.libgdx.entity.Entity;
import tiltadv.components.display.SpriteComponent;
import tiltadv.components.input.TouchableComponent;
import tiltadv.globals.Events;

/**
 * Any entity that can be selected and thereby targetted.
 */
public final class TargetTouchableComponent extends TouchableComponent {

    private Entity owner;
    private SpriteComponent spriteComponent;

    @Override
    protected void handleInitialized(final Entity owner) {
        this.owner = owner;
        spriteComponent = owner.requireComponent(SpriteComponent.class);
    }

    @Override
    protected void handleReset() {
        owner = null;
        spriteComponent = null;
    }

    @Override
    protected void handleTouched() {
        Events.onTargetted.fire(owner);
    }


}
