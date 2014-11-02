package tiltadv.components.body;

import com.badlogic.gdx.math.Vector2;
import dhcoder.libgdx.entity.AbstractComponent;

/**
 * A simple component that encapsulates an entity's position.
 */
public final class PositionComponent extends AbstractComponent {

    private final Vector2 position = new Vector2();

    public PositionComponent setPosition(final Vector2 position) {
        this.position.set(position);
        return this;
    }

    public Vector2 getPosition() {
        return position;
    }
}
