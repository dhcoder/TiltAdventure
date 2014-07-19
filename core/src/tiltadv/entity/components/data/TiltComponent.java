package tiltadv.entity.components.data;

import com.badlogic.gdx.math.Vector2;
import tiltadv.entity.AbstractComponent;
import tiltadv.immutable.ImmutableVector2;

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

    public ImmutableVector2 getTilt() { return immutableTiltVector; }

    public void setTilt(final ImmutableVector2 tiltVector) {
        this.tiltVector.set(tiltVector.getX(), tiltVector.getY());
        this.tiltVector.limit(MAX_TILT_VECTOR_LEN);
    }
}