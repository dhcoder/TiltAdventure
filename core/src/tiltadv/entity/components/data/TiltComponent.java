package tiltadv.entity.components.data;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import dhcoder.support.math.Angle;
import dhcoder.support.time.Duration;
import tiltadv.entity.AbstractComponent;
import tiltadv.entity.Entity;
import tiltadv.immutable.ImmutableAngle;
import tiltadv.immutable.ImmutableVector2;

import static com.badlogic.gdx.Input.Keys;

/**
 * Component which exposes a vector that represents the device's tilt orientation (for landscape mode).
 */
public class TiltComponent extends AbstractComponent {

    // Most accelerometer readouts allow maximum 2f in each direction, so if the vector is [2f, 2f], that has a length
    // of √(2*2 + 2*2) -> √8 -> ~2.8. Let's limit to that, then, in case some random device allows much higher tilt
    // values...
    private static final float MAX_TILT_VECTOR_LEN = 2.8f;

    private final Vector2 tiltVector = new Vector2();
    private final ImmutableVector2 immutableTiltVector = new ImmutableVector2(tiltVector);

    // For testing only, we can use the keyboard to "tilt" the device
    private boolean useKeyboardOverride = false;

    public ImmutableVector2 getTiltVector() {
        return immutableTiltVector;
    }

    @Override
    public void initialize(final Entity owner) {
        useKeyboardOverride = (Gdx.app.getType() != Application.ApplicationType.Android);
    }

    @Override
    public void update(final Duration elapsedTime) {

        if (!useKeyboardOverride) {
            // Convert portrait accelerometer directions to landscape
            // See https://github.com/libgdx/libgdx/wiki/Accelerometer
            tiltVector.set(Gdx.input.getAccelerometerY(), -Gdx.input.getAccelerometerX());
        }
        else { // useKeyboardOverride
            tiltVector.setZero();

            if (Gdx.input.isKeyPressed(Keys.DPAD_LEFT)) { tiltVector.x = -2f; }
            else if (Gdx.input.isKeyPressed(Keys.DPAD_RIGHT)) { tiltVector.x = 2f; }

            if (Gdx.input.isKeyPressed(Keys.DPAD_DOWN)) { tiltVector.y = -2f; }
            else if (Gdx.input.isKeyPressed(Keys.DPAD_UP)) { tiltVector.y = 2f; }

        }

        tiltVector.limit(MAX_TILT_VECTOR_LEN);
    }
}
