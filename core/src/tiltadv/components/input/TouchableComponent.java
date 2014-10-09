package tiltadv.components.input;

import com.badlogic.gdx.math.Vector2;
import dhcoder.libgdx.collision.shape.Circle;
import dhcoder.libgdx.entity.AbstractComponent;
import dhcoder.libgdx.entity.Entity;
import tiltadv.components.body.PositionComponent;

/**
 * A component to attach to an entity that can be
 */
public abstract class TouchableComponent extends AbstractComponent {

    private static final Circle TOUCH_AREA = new Circle(10.0f);
    private Entity owner;
    private PositionComponent positionComponent;

    protected Entity getOwner() {
        return owner;
    }

    public final boolean handleIfTouched(final Vector2 touchPosition) {
        Vector2 position = positionComponent.getPosition();
        if (!TOUCH_AREA.containsPoint(touchPosition.x - position.x, touchPosition.y - position.y)) {
            return false;
        }

        handleTouched();
        return true;
    }

    @Override
    public final void initialize(final Entity owner) {
        this.owner = owner;
        positionComponent = owner.requireComponent(PositionComponent.class);
    }

    @Override
    public final void reset() {
        owner = null;
        positionComponent = null;
    }

    protected abstract void handleTouched();

    protected void handleInitialize() {}

    protected void handleReset() {}
}
