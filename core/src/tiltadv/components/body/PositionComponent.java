package tiltadv.components.body;

import com.badlogic.gdx.math.Vector2;
import dhcoder.libgdx.entity.AbstractComponent;
import dhcoder.libgdx.entity.Entity;
import dhcoder.support.event.Event;
import dhcoder.support.time.Duration;

/**
 * A component that encapsulates an entity's position, scale, and rotation values.
 */
public abstract class PositionComponent extends AbstractComponent {

    public final Event onChanged = new Event();
    private final Vector2 position = new Vector2();

    public Vector2 getPosition() {
        return position;
    }

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
        position.set(0f, 0f);
        onChanged.clearListeners();

        handleReset();
    }

    protected PositionComponent setPositionInternal(final Vector2 position) {
        this.position.set(position);
        return this;
    }

    protected void handleConstruction() {}

    protected void handleInitialize(final Entity owner) {}

    protected void handleUpdate(final Duration elapsedTime) {}

    protected void handleReset() {}

}
