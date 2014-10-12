package tiltadv.components.body;

import com.badlogic.gdx.math.Vector2;
import dhcoder.libgdx.entity.AbstractComponent;
import dhcoder.support.event.Event;

/**
 * A component that encapsulates an entity's position, scale, and rotation values.
 */
public final class PositionComponent extends AbstractComponent {

    public final Event onChanged = new Event();

    private final Vector2 position = new Vector2();

    public PositionComponent() { reset(); }

    public Vector2 getPosition() {
        return position;
    }

    public PositionComponent setPosition(final Vector2 position) {
        return setPosition(position, true);
    }

    public PositionComponent setPosition(final Vector2 translate, final boolean sendUpdate) {
        if (this.position.equals(translate)) {
            return this;
        }

        this.position.set(translate);
        if (sendUpdate) {
            onChanged.fire(this);
        }
        return this;
    }

    @Override
    public void reset() {
        position.set(0f, 0f);
        onChanged.clearListeners();
    }
}
