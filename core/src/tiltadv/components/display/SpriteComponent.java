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
import dhcoder.support.opt.OptFloat;
import tiltadv.components.body.TransformComponent;
import tiltadv.globals.Services;

/**
 * A component that encapsulates the logic of rendering a sprite.
 * <p/>
 */
public final class SpriteComponent extends AbstractComponent implements Renderable {

    /**
     * The source sprite used by this component. Use {@link Sprite#set(Sprite)} if you need to change it, later.
     */
    private final Sprite sprite;
    private boolean hidden;
    private final OptFloat zOpt = OptFloat.withNoValue();

    private TransformComponent transformComponent;

    public SpriteComponent() {
        sprite = new Sprite();
    }

    public SpriteComponent setTextureRegion(final TextureRegion textureRegion) {
        sprite.setRegion(textureRegion);
        sprite.setSize(textureRegion.getRegionWidth(), textureRegion.getRegionHeight());
        sprite.setOrigin(sprite.getWidth() / 2f, sprite.getHeight() / 2f);

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

        RenderSystem renderSystem = Services.get(RenderSystem.class);
        renderSystem.add(this);
    }

    @Override
    public void render(final Batch batch) {
        if (hidden) { return; }
        if (sprite.getTexture() == null) { return; }

        Vector2 translate = transformComponent.getTranslate();
        Vector2 scale = transformComponent.getScale();
        Angle rotation = transformComponent.getRotation();

        sprite.setCenter(translate.x, translate.y);
        sprite.setScale(scale.x, scale.y);
        sprite.setRotation(rotation.getDegrees());

        sprite.draw(batch);
    }

    @Override
    public float getZ() {
        if (zOpt.hasValue()) {
            return zOpt.getValue();
        }
        // The lower the sprite is on the screen, the higher its z-value is. Ex: Standing at y = 5 means you are in
        // front of an object at y = 20.
        return -(sprite.getY() - sprite.getHeight());
    }

    public void setZ(final float z) {
        zOpt.set(z);
    }

    @Override
    public void reset() {
        sprite.setTexture(null);
        hidden = false;
        zOpt.reset();

        transformComponent = null;

        RenderSystem renderSystem = Services.get(RenderSystem.class);
        renderSystem.remove(this);
    }
}
