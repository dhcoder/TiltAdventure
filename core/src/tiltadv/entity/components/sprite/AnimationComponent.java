package tiltadv.entity.components.sprite;

import tiltadv.entity.AbstractComponent;
import tiltadv.entity.Entity;

public final class AnimationComponent extends AbstractComponent {

    private SpriteComponent spriteComponent;

    @Override
    public void initialize(final Entity owner) {
        owner.requireSingleInstance(AnimationComponent.class);
        spriteComponent = owner.requireComponent(SpriteComponent.class);
    }
}
