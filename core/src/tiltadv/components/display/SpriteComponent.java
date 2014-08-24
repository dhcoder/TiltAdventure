package tiltadv.components.display;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import dhcoder.libgdx.entity.AbstractComponent;
import dhcoder.libgdx.entity.Entity;
import dhcoder.support.math.Angle;
import dhcoder.support.time.Duration;
import tiltadv.components.model.SizeComponent;
import tiltadv.components.model.TransformComponent;

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
    private final Sprite sprite;
    private boolean hidden;

    private TransformComponent transformComponent;
    private SizeComponent sizeComponent;

    public SpriteComponent() {
        sprite = new Sprite();
    }

    public SpriteComponent(final TextureRegion textureRegion) {
        this();
        setTextureRegion(textureRegion);
    }

    public void setTextureRegion(final TextureRegion textureRegion) {
        sprite.setRegion(textureRegion);
        sprite.setOrigin(textureRegion.getRegionWidth() / 2f, textureRegion.getRegionHeight() / 2f);
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
    public void update(final Duration elapsedTime) {
        Vector2 translate = transformComponent.getTranslate();
        Vector2 scale = transformComponent.getScale();
        Angle rotation = transformComponent.getRotation();
        Vector2 size = sizeComponent.getSize();

        sprite.setCenter(translate.x, translate.y);
        sprite.setSize(size.x, size.y);
        sprite.setScale(scale.x, scale.y);
        sprite.setRotation(rotation.getDegrees());
    }

    @Override
    public void render(final Batch batch) {
        if (hidden) { return; }
        sprite.draw(batch);
    }

    @Override
    protected void resetComponent() {
        sprite.setTexture(null);
        hidden = false;
    }
}
