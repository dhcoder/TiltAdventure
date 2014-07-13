package tiltadv.entity.components.data;

import com.badlogic.gdx.math.Vector2;
import dhcoder.support.opt.Opt;
import tiltadv.entity.AbstractComponent;

/**
 * A component that encapsulates an entity's size.
 */
public final class SizeComponent extends AbstractComponent {

    public final Vector2 size = new Vector2(0f, 0f);

    public SizeComponent() {}

    public SizeComponent(final Vector2 size) {
        this.size.set(size);
    }

    public SizeComponent(final float width, final float height) {
        this.size.set(width, height);
    }

}
