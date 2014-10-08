package tiltadv.components.input;

import com.badlogic.gdx.math.Vector2;
import dhcoder.libgdx.collision.shape.Circle;
import dhcoder.libgdx.entity.AbstractComponent;
import dhcoder.libgdx.entity.Entity;
import tiltadv.components.body.PositionComponent;

/**
 * A component to attach to an entity that can be
 */
public final class TouchableComponent extends AbstractComponent {

    private static final Circle TOUCH_AREA = new Circle(10.0f);
    private PositionComponent positionComponent;

    public boolean isTouched(final Vector2 touchPosition) {
        Vector2 position = positionComponent.getPosition();
        return TOUCH_AREA.containsPoint(touchPosition.x - position.x, touchPosition.y - position.y);
    }

    @Override
    public void initialize(final Entity owner) {
        positionComponent = owner.requireComponent(PositionComponent.class);
    }

    @Override
    public void reset() {
        positionComponent = null;
    }
}
