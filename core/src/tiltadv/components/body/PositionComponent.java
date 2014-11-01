package tiltadv.components.body;

import com.badlogic.gdx.math.Vector2;
import dhcoder.libgdx.entity.AbstractComponent;
import dhcoder.libgdx.entity.Entity;
import dhcoder.support.time.Duration;

/**
 * A component that encapsulates an entity's position, scale, and rotation values.
 */
public abstract class PositionComponent<T extends PositionComponent<T>> extends AbstractComponent {

    public abstract T setPosition(final Vector2 position);
    public abstract Vector2 getPosition();

    @Override
    public final void initialize(final Entity owner) {
        handleInitialize(owner);
    }

    @Override
    public final void update(final Duration elapsedTime) {
        handleUpdate(elapsedTime);
    }

    @Override
    public final void reset() {
        handleReset();
    }

    protected void handleInitialize(final Entity owner) {}

    protected void handleUpdate(final Duration elapsedTime) {}

    protected void handleReset() {}

}
