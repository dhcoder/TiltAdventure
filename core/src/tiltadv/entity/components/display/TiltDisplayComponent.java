package tiltadv.entity.components.display;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import dhcoder.support.math.Angle;
import dhcoder.support.time.Duration;
import tiltadv.entity.AbstractComponent;
import tiltadv.entity.Entity;
import tiltadv.entity.components.input.TiltComponent;
import tiltadv.entity.components.model.TransformComponent;
import tiltadv.memory.Pools;

/**
 * Component which sets the transform of an entity to match the direction of the hardware's tilt.
 */
public final class TiltDisplayComponent extends AbstractComponent {

    private final Sprite arrowSprite;
    private final Entity observedEntity;
    private TransformComponent transformComponent;
    private SpriteComponent spriteComponent;
    private TiltComponent tiltComponent;

    /**
     * Create a tilt indicator by passing in a sprite which represents an arrow facing straight right. This component
     * will rotate and render the arrow appropriately.
     */
    public TiltDisplayComponent(final Sprite arrowSprite, final Entity observedEntity) {
        this.arrowSprite = arrowSprite;
        this.observedEntity = observedEntity;
    }

    @Override
    public void initialize(final Entity owner) {
        transformComponent = owner.requireComponent(TransformComponent.class);
        spriteComponent = owner.requireComponent(SpriteComponent.class);
        spriteComponent.setSprite(arrowSprite);

        tiltComponent = observedEntity.requireComponent(TiltComponent.class);
    }

    @Override
    public void update(final Duration elapsedTime) {
        Vector2 tilt = tiltComponent.getTilt();
        if (tilt.isZero(1f)) {
            spriteComponent.setHidden(true);
            return;
        }

        spriteComponent.setHidden(false);
        {
            Angle angle = Pools.angles.grabNew();
            angle.setDegrees(tilt.angle());
            transformComponent.setRotation(angle);
            Pools.angles.free(angle);
        }
    }
}
