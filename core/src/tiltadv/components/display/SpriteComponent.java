package tiltadv.components.display;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import dhcoder.libgdx.entity.AbstractComponent;
import dhcoder.libgdx.entity.Entity;
import dhcoder.libgdx.render.RenderSystem;
import dhcoder.libgdx.render.Renderable;
import dhcoder.support.math.Angle;
import dhcoder.support.time.Duration;
import tiltadv.components.body.SizeComponent;
import tiltadv.components.body.TransformComponent;
import tiltadv.globals.Services;

/**
 * A component that encapsulates the logic of rendering a sprite.
 * <p/>
 * If a {@link SizeComponent} is available on the owning {@link Entity}, the sprite will render itself using that value.
 * Otherwise, it will render to the size of the current sprite.
 */
public final class SpriteComponent extends AbstractComponent implements Renderable {

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

    public SpriteComponent setTextureRegion(final TextureRegion textureRegion) {
        sprite.setRegion(textureRegion);
        sprite.setOrigin(textureRegion.getRegionWidth() / 2f, textureRegion.getRegionHeight() / 2f);
        return this;
    }

    public SpriteComponent setHidden(final boolean hidden) {
        this.hidden = hidden;
        return this;
    }

    public SpriteComponent setTint(final Color tint) {
        sprite.setColor(tint);
        return this;
    }

    public SpriteComponent setAlpha(final float alpha) {
        sprite.setAlpha(alpha);
        return this;
    }

    @Override
    public void initialize(final Entity owner) {
        transformComponent = owner.requireComponent(TransformComponent.class);
        sizeComponent = owner.requireComponent(SizeComponent.class);

        RenderSystem renderSystem = Services.get(RenderSystem.class);
        renderSystem.add(this);
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
        if (sprite.getTexture() == null) { return; }
        sprite.draw(batch);
    }

    @Override
    public float getZ() {
        // The lower the sprite is on the screen, the higher its z-value is. Ex: Standing at y = 5 means you are in
        // front of an object at y = 20.
        return -(sprite.getY() - sprite.getHeight());
    }

    @Override
    public void reset() {
        sprite.setTexture(null);
        hidden = false;

        sizeComponent = null;
        transformComponent = null;

        RenderSystem renderSystem = Services.get(RenderSystem.class);
        renderSystem.remove(this);
    }
}
