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
import tiltadv.components.body.PositionComponent;
import tiltadv.globals.Services;
import tiltadv.memory.Pools;

/**
 * A component that encapsulates the logic of rendering a sprite.
 * <p/>
 */
public final class SpriteComponent extends AbstractComponent implements Renderable {

    private final Sprite sprite = new Sprite();
    private final Vector2 offset = new Vector2();
    private boolean hidden;
    private final OptFloat zOpt = OptFloat.withNoValue();

    private PositionComponent positionComponent;

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

    public SpriteComponent setRotation(final Angle angle) {
        sprite.setRotation(angle.getDegrees());
        return this;
    }

    public SpriteComponent setAlpha(final float alpha) {
        sprite.setAlpha(alpha);
        return this;
    }

    public SpriteComponent setOffset(final Vector2 offset) {
        offset.set(offset);
        return this;
    }

    @Override
    public void initialize(final Entity owner) {
        positionComponent = owner.requireComponent(PositionComponent.class);

        RenderSystem renderSystem = Services.get(RenderSystem.class);
        renderSystem.add(this);
    }

    @Override
    public void render(final Batch batch) {
        if (hidden) { return; }
        if (sprite.getTexture() == null) { return; }

        Vector2 translate = Pools.vector2s.grabNew().set(positionComponent.getPosition());
        translate.add(offset);

        sprite.setCenter(translate.x, translate.y);
        sprite.draw(batch);

        Pools.vector2s.freeCount(1);
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
        offset.setZero();
        sprite.setTexture(null);
        hidden = false;
        zOpt.reset();

        positionComponent = null;

        RenderSystem renderSystem = Services.get(RenderSystem.class);
        renderSystem.remove(this);
    }
}
