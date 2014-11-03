package tiltadv.components.dynamics;

import com.badlogic.gdx.math.Vector2;
import dhcoder.libgdx.entity.AbstractComponent;

/**
 * A simple component that encapsulates an entity's position.
 */
public final class PositionComponent extends AbstractComponent {
    private final Vector2 position = new Vector2();

    public Vector2 getPosition() {
        return position;
    }

    public PositionComponent setPosition(final Vector2 position) {
        this.position.set(position);
        return this;
    }

    @Override
    public void reset() {
        position.setZero();
    }
}
