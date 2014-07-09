package tiltadv.components;

import com.badlogic.gdx.math.Vector2;
import tiltadv.entity.AbstractComponent;

/**
 * A component that encapsulates an entity's position and size.
 */
public final class LocationComponent extends AbstractComponent {

    private Vector2 pos = new Vector2();
    private Vector2 size = new Vector2();

    public LocationComponent(Vector2 pos, Vector2 size) {
        this.pos.set(pos);
        this.size.set(size);
    }
}
