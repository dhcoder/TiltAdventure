package tiltadv.components.model;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import dhcoder.libgdx.entity.AbstractComponent;

/**
 * A component that encapsulates an entity's size.
 */
public final class SizeComponent extends AbstractComponent {

    private final Vector2 size = new Vector2();

    public Vector2 getSize() {
        return size;
    }

    public SizeComponent setSize(final Vector2 size) {
        this.size.set(size);
        return this;
    }

    /**
     * Convenience method which sets this size from a Sprite's dimensions.
     */
    public SizeComponent setSizeFrom(final TextureRegion textureRegion) {
        this.size.set(textureRegion.getRegionWidth(), textureRegion.getRegionHeight());
        return this;
    }

    @Override
    public void reset() {
        size.setZero();
    }
}
