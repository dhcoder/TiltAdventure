package tiltadv.entity.components.controllers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import dhcoder.support.time.Duration;
import tiltadv.entity.AbstractComponent;
import tiltadv.entity.Entity;
import tiltadv.entity.components.data.TiltComponent;

/**
 * Component which reads the device's accelerometer and sets the entity's tilt value accordingly.
 */
public class AccelerometerComponent extends AbstractComponent {

    // If the tilt vector is smaller than the following value, we consider the amount of tilt too weak to count, and
    // instead just treat it as no tilt at all
    private static final float TILT_THRESHOLD = 1.3f;

    private final Vector2 tilt = new Vector2(); // TODO: Replace with pool
    private TiltComponent tiltComponent;

    @Override
    public void initialize(final Entity owner) {
        tiltComponent = owner.requireComponent(TiltComponent.class);
    }

    @Override
    public void update(final Duration elapsedTime) {

        // Convert portrait accelerometer directions to landscape
        // See https://github.com/libgdx/libgdx/wiki/Accelerometer
        tilt.set(Gdx.input.getAccelerometerY(), -Gdx.input.getAccelerometerX());
        if (tilt.isZero(TILT_THRESHOLD)) {
            tilt.setZero();
        }

        tiltComponent.setTilt(tilt);
    }
}
