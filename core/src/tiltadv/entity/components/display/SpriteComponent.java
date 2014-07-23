package tiltadv.entity.components.display;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import dhcoder.support.math.Angle;
import tiltadv.entity.AbstractComponent;
import tiltadv.entity.Entity;
import tiltadv.entity.components.data.SizeComponent;
import tiltadv.entity.components.data.TransformComponent;

/**
 * A component that encapsulates the logic of rendering a sprite.
 * <p/>
 * If a {@link SizeComponent} is available on the owning {@link Entity}, the sprite will render itself using that value.
 * Otherwise, it will render to the size of the current sprite.
 */
public final class SpriteComponent extends AbstractComponent {

    /**
     * The source sprite used by this component. Use {@link Sprite#set(Sprite)} if you need to change it, later.
     */
    private Sprite sprite;
    private boolean hidden;
    private TransformComponent transformComponent;
    private SizeComponent sizeComponent;

    public SpriteComponent() {
        sprite = new Sprite();
    }

    public SpriteComponent(final Sprite sprite) {
        setSprite(sprite);
    }

    public Sprite getSprite() {
        return sprite;
    }

    public void setSprite(final Sprite sprite) {
        this.sprite = sprite;
    }

    public void setTextureRegion(final TextureRegion textureRegion) {
        sprite.setRegion(textureRegion);
    }

    public boolean isHidden() {
        return hidden;
    }

    public void setHidden(final boolean hidden) {
        this.hidden = hidden;
    }

    @Override
    public void initialize(final Entity owner) {
        owner.requireSingleInstance(SpriteComponent.class);

        transformComponent = owner.requireComponent(TransformComponent.class);
        sizeComponent = owner.requireComponent(SizeComponent.class);
    }

    @Override
    public void render(final Batch batch) {

        if (hidden) { return; }

        Vector2 translate = transformComponent.getTranslate();
        Vector2 scale = transformComponent.getScale();
        Angle rotation = transformComponent.getRotation();
        Vector2 size = sizeComponent.getSize();

        batch.draw(sprite, translate.x, translate.y, sprite.getOriginX(), sprite.getOriginY(), size.x, size.y,
            scale.x, scale.y, rotation.getDegrees());
    }
}
