package tiltadv.components.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import dhcoder.libgdx.entity.AbstractComponent;
import dhcoder.libgdx.entity.Entity;
import dhcoder.support.time.Duration;
import tiltadv.components.dynamics.TiltComponent;
import tiltadv.memory.Pools;

/**
 * Component which reads the device's accelerometer and sets the entity's tilt value accordingly.
 */
public final class AccelerometerInputComponent extends AbstractComponent {

    // If the tilt vector is smaller than the following value, we consider the amount of tilt too weak to count, and
    // instead just treat it as no tilt at all
    private static final float TILT_THRESHOLD = 0.1f;
    private static final float ANGLE_TO_TILT = -0.1f;
    // Default screen position is slightly tilted toward the player, about 10° from flat
    // TODO: Allow multiple configuration, maybe? (Flat, upside-down, configurable?)
    private final Vector3 referenceVector = new Vector3(.4f, 0f, 1f).nor();
    private TiltComponent tiltComponent;

    @Override
    public void initialize(final Entity owner) {
        tiltComponent = owner.requireComponent(TiltComponent.class);
    }

    @Override
    public void update(final Duration elapsedTime) {
        Vector2 tilt = Pools.vector2s.grabNew();

        //if (isTiltActivated) {
        //TODO: Right now, we're always having tilt activated, but eventually, we need to be able to stop the character
        // from moving if in defense mode...
        if (true) {
            Vector3 accelerometerCurr = Pools.vector3s.grabNew();
            Vector3 accelerometerCurrNormalized = Pools.vector3s.grabNew();
            Quaternion rotationBetweenVectors = Pools.quaternions.grabNew();

            readAccelerometerValuesInto(accelerometerCurr);
            accelerometerCurrNormalized.set(accelerometerCurr).nor();
            rotationBetweenVectors.setFromCross(referenceVector, accelerometerCurrNormalized);

            tilt.set(angleToTilt(rotationBetweenVectors.getPitch()), angleToTilt(rotationBetweenVectors.getYaw()));

            if (tilt.isZero(TILT_THRESHOLD)) {
                tilt.setZero();
            }

            Pools.quaternions.free(rotationBetweenVectors);
            Pools.vector3s.free(accelerometerCurrNormalized);
            Pools.vector3s.free(accelerometerCurr);
        }

        tiltComponent.setTilt(tilt);
        Pools.vector2s.free(tilt);
    }

    @Override
    public void reset() {
        tiltComponent = null;
    }

    // Given an angle, convert it to proper values to put into a vector that represents tilt magnitude
    private float angleToTilt(final float angleDeg) {
        return angleDeg * ANGLE_TO_TILT;
    }

    private void readAccelerometerValuesInto(final Vector3 outVector) {
        outVector.set(Gdx.input.getAccelerometerX(), Gdx.input.getAccelerometerY(), Gdx.input.getAccelerometerZ());
    }

}
