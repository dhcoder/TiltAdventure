package tiltadv.entity.components.sprite;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import dhcoder.support.opt.Opt;
import tiltadv.entity.AbstractComponent;
import tiltadv.entity.Entity;
import tiltadv.entity.components.data.SizeComponent;
import tiltadv.entity.components.data.TransformComponent;

/**
 * A component that encapsulates the logic of rendering a sprite.
 *
 * If a {@link SizeComponent} is available on the owning {@link Entity}, the sprite will render itself using that value.
 * Otherwise, it will render to the size of the current sprite.
 */
public class SpriteComponent extends AbstractComponent {

    /**
     * The source sprite used by this component. Use {@link Sprite#set(Sprite)} if you need to change it, later.
     */
    public final Sprite sprite = new Sprite();

    private TransformComponent transformComponent;
    private SizeComponent sizeComponent;

    public SpriteComponent() {}

    public SpriteComponent(final Sprite sprite) {
        this.sprite.set(sprite);
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
