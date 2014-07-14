package tiltadv.entity.components.util;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import tiltadv.entity.AbstractComponent;
import tiltadv.entity.Entity;
import tiltadv.entity.components.data.TiltComponent;
import tiltadv.entity.components.data.TransformComponent;
import tiltadv.entity.components.sprite.SpriteComponent;

/**
 * Component which sets the transform of an entity to match the direction of the hardware's tilt.
 */
public class TiltDisplayComponent extends AbstractComponent {

    /**
     * Threshold for tilt strength. If the tilt vector is lower than this value, it means the user isn't tilting enough
     * and we should ignore the input.
     */
    private static final float TILT_THRESHOLD = 1.4f;
    private final Sprite arrowSprite;
    private TransformComponent transformComponent;
    private TiltComponent tiltComponent;

    /**
     * Create a tilt indicator by passing in a sprite which represents an arrow facing straight right. This component
     * will rotate and render the arrow appropriately.
     */
    public TiltDisplayComponent(final Sprite arrowSprite) {
        this.arrowSprite = arrowSprite;
    }

    @Override
    public void initialize(final Entity owner) {
        transformComponent = owner.requireComponent(TransformComponent.class);
        tiltComponent = owner.requireComponent(TiltComponent.class);

        SpriteComponent spriteComponent = owner.requireComponent(SpriteComponent.class);
        spriteComponent.sprite.set(arrowSprite);
    }

    @Override
    public void render(final Batch batch) {
        Vector2 tiltVector = tiltComponent.getTiltVector();
        transformComponent.rotation.setDegrees(tiltVector.angle());
    }
}
