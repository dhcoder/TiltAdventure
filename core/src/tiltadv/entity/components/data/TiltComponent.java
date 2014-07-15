package tiltadv.entity.components.data;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import dhcoder.support.time.Duration;
import tiltadv.entity.AbstractComponent;

import static com.badlogic.gdx.Input.Keys;

/**
 * Component which exposes a vector that represents the device's tilt orientation (for landscape mode).
 */
public class TiltComponent extends AbstractComponent {

    // For testing, use the keyboard to "tilt" the device
    private static final boolean KEYBOARD_OVERRIDE = true;
    private final Vector2 tiltVector = new Vector2();

    public Vector2 getTiltVector() {
        return tiltVector.cpy();
    }

    @Override
    public void update(final Duration elapsedTime) {

        if (KEYBOARD_OVERRIDE) {
            tiltVector.setZero();
            if(Gdx.input.isKeyPressed(Keys.DPAD_LEFT))
                tiltVector.x = -2f;
            else if(Gdx.input.isKeyPressed(Keys.DPAD_RIGHT))
                tiltVector.x = 2f;
            if(Gdx.input.isKeyPressed(Keys.DPAD_DOWN))
                tiltVector.y = -2f;
            if(Gdx.input.isKeyPressed(Keys.DPAD_UP))
                tiltVector.y = 2f;

            return;
        }
        // Convert portrait accelerometer directions to landscape
        // See https://github.com/libgdx/libgdx/wiki/Accelerometer
        tiltVector.set(Gdx.input.getAccelerometerY(), -Gdx.input.getAccelerometerX());


    }
}
