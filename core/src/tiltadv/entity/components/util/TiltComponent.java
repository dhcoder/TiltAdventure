package tiltadv.entity.components.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import tiltadv.entity.AbstractComponent;
import tiltadv.entity.Entity;
import tiltadv.entity.components.data.TransformComponent;

import static com.badlogic.gdx.math.MathUtils.atan2;

/**
 * Component which sets the transform of an entity to match the direction of the hardware's tilt.
 */
public class TiltComponent extends AbstractComponent {

    /**
     * Threshold for tilt strength. If the tilt vector is lower than this value, it means the user isn't tilting enough
     * and we should ignore the input.
     */
    private static final float TILT_THRESHOLD = 1.4f;
    private final Vector2 accelVector = new Vector2();
    private TransformComponent transformComponent;

    @Override
    public void initialize(final Entity owner) {
        transformComponent = owner.requireComponent(TransformComponent.class);
    }

    @Override
    public void render(final Batch batch) {
        // Convert portrait accelerometer directions to landscape
        accelVector.set(-Gdx.input.getAccelerometerY(), -Gdx.input.getAccelerometerX());
        if (accelVector.len2() < TILT_THRESHOLD) { return; }

        transformComponent.rotation.setRadians(atan2(accelVector.x, accelVector.y));
    }
}
