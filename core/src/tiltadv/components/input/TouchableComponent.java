package tiltadv.components.input;

import com.badlogic.gdx.math.Vector2;
import dhcoder.libgdx.entity.AbstractComponent;
import dhcoder.libgdx.entity.Entity;
import dhcoder.support.shape.Circle;
import tiltadv.components.dynamics.PositionComponent;
import tiltadv.globals.Services;
import tiltadv.input.TouchSystem;

/**
 * A component to attach to an entity that can be
 */
public abstract class TouchableComponent extends AbstractComponent {

    private static final Circle TOUCH_AREA = new Circle(20f);
    private PositionComponent positionComponent;
    private boolean selected;

    public final boolean select(final Vector2 touchPosition) {
        Vector2 position = positionComponent.getPosition();
        if (!TOUCH_AREA.containsPoint(touchPosition.x - position.x, touchPosition.y - position.y)) {
            return false;
        }

        handleSelected();
        selected = true;
        return true;
    }

    public void deselect() {
        if (selected) {
            handleDeselected();
            selected = false;
        }
    }

    @Override
    public final void initialize(final Entity owner) {
        positionComponent = owner.requireComponent(PositionComponent.class);

        final TouchSystem touchSystem = Services.get(TouchSystem.class);
        touchSystem.add(this);

        handleInitialized(owner);
    }

    @Override
    public final void reset() {
        deselect();

        final TouchSystem touchSystem = Services.get(TouchSystem.class);
        touchSystem.remove(this);

        positionComponent = null;

        handleReset();
    }

    protected abstract void handleSelected();
    protected abstract void handleDeselected();

    protected void handleInitialized(final Entity owner) {}

    protected void handleReset() {}
}
