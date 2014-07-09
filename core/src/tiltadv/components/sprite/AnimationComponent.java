package tiltadv.components.sprite;

import tiltadv.entity.AbstractComponent;
import tiltadv.entity.Entity;

public final class AnimationComponent extends AbstractComponent {

    private SpriteComponent spriteComponent;

    @Override
    public void initialize(final Entity owner) {
        spriteComponent = owner.requireSingleInstance(SpriteComponent.class);
    }
}
