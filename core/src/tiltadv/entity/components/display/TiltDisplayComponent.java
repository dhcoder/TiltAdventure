package tiltadv.entity.components.display;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import tiltadv.entity.AbstractComponent;
import tiltadv.entity.Entity;
import tiltadv.entity.components.data.TiltComponent;
import tiltadv.entity.components.data.TransformComponent;
import tiltadv.entity.components.sprite.SpriteComponent;
import tiltadv.immutable.ImmutableVector2;

/**
 * Component which sets the transform of an entity to match the direction of the hardware's tilt.
 */
public class TiltDisplayComponent extends AbstractComponent {

    private final Sprite arrowSprite;
    private TransformComponent transformComponent;
    private SpriteComponent spriteComponent;

    private Entity observedEntity;
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
        spriteComponent.sprite.set(arrowSprite);

        tiltComponent = observedEntity.requireComponent(TiltComponent.class);
    }

    @Override
    public void render(final Batch batch) {
        ImmutableVector2 tilt = tiltComponent.getTilt();
        if (tilt.isZero(1f)) {
            spriteComponent.hidden = true;
            return;
        }

        spriteComponent.hidden = false;
        transformComponent.rotation.setDegrees(tilt.getAngle());
    }
}
