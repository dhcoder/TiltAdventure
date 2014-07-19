package tiltadv.entity.components.data;

import com.badlogic.gdx.math.Vector2;
import tiltadv.entity.AbstractComponent;
import tiltadv.immutable.ImmutableSprite;

/**
 * A component that encapsulates an entity's size.
 */
public final class SizeComponent extends AbstractComponent {

    /**
     * Convenience method which generates a new size component from a Sprite's dimensions.
     */
    public static SizeComponent fromSprite(final ImmutableSprite sprite) {
        return new SizeComponent(sprite.getWidth(), sprite.getHeight());
    }

    public final Vector2 size = new Vector2(0f, 0f);

    public SizeComponent() {}

    public SizeComponent(final Vector2 size) {
        this.size.set(size);
    }

    public SizeComponent(final float width, final float height) {
        this.size.set(width, height);
    }

}
