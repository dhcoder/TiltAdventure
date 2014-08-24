package tiltadv.components.model;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import dhcoder.libgdx.entity.AbstractComponent;

/**
 * A component that encapsulates an entity's size.
 */
public final class SizeComponent extends AbstractComponent {

    /**
     * Convenience method which generates a new size component from a Sprite's dimensions.
     */
    public static SizeComponent from(final TextureRegion textureRegion) {
        return new SizeComponent(textureRegion.getRegionWidth(), textureRegion.getRegionHeight());
    }

    private final Vector2 size = new Vector2(0f, 0f);

    public SizeComponent() {}

    public SizeComponent(final Vector2 size) {
        this.size.set(size);
    }

    public SizeComponent(final float width, final float height) {
        this.size.set(width, height);
    }

    public Vector2 getSize() {
        return size;
    }

    public void setSize(final Vector2 size) {
        this.size.set(size);
    }

    @Override
    protected void resetComponent() {
        size.setZero();
    }
}
