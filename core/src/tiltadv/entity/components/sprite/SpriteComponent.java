package tiltadv.entity.components.sprite;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import tiltadv.entity.AbstractComponent;
import tiltadv.entity.Entity;
import tiltadv.entity.components.SizeComponent;
import tiltadv.entity.components.TransformComponent;

/**
 * A component that encapsulates the logic of rendering a sprite.
 */
public class SpriteComponent extends AbstractComponent {

    public Sprite sprite;

    private TransformComponent transformComponent;
    private SizeComponent sizeComponent;

    public SpriteComponent(final Sprite sprite) {
        this.sprite = sprite;
    }

    @Override
    public void initialize(final Entity owner) {
        owner.requireSingleInstance(SpriteComponent.class);

        transformComponent = owner.requireComponent(TransformComponent.class);
        sizeComponent = owner.requireComponent(SizeComponent.class);
    }

    @Override
    public void render(final Batch batch) {
        batch.draw(sprite, transformComponent.translate.x, transformComponent.translate.y, sprite.getWidth() / 2,
            sprite.getHeight() / 2, sizeComponent.size.x, sizeComponent.size.y, transformComponent.scale.x,
            transformComponent.scale.y, transformComponent.rotation.getDegrees());
    }
}
