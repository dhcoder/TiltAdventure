package tiltadv.entity.components.data;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import tiltadv.entity.AbstractComponent;

/**
 * Component which exposes a vector that represents the device's tilt orientation (for landscape mode).
 */
public class TiltComponent extends AbstractComponent {

    private final Vector2 tiltVector = new Vector2();

    public Vector2 getTiltVector() {
        return tiltVector.cpy();
    }

    @Override
    public void update(final float elapsedTime) {
        // Convert portrait accelerometer directions to landscape
        // See https://github.com/libgdx/libgdx/wiki/Accelerometer
        tiltVector.set(Gdx.input.getAccelerometerY(), -Gdx.input.getAccelerometerX());
    }
}
