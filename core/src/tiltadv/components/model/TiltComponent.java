package tiltadv.components.model;

import com.badlogic.gdx.math.Vector2;
import dhcoder.libgdx.entity.AbstractComponent;
import dhcoder.libgdx.entity.Entity;

/**
 * Component which exposes a vector that represents the device's tilt orientation (for landscape mode).
 */
public final class TiltComponent extends AbstractComponent {

    // Most accelerometer readouts allow maximum 2f in each direction, so if the vector is [2f, 2f], that has a length
    // of √(2*2 + 2*2) -> √8 -> ~2.8. Let's limit to that, then, in case some random device allows much higher tilt
    // values...
    private static final float MAX_TILT_VECTOR_LEN = 2.8f;
    private final Vector2 tilt = new Vector2();

    private HeadingComponent headingComponent;

    public TiltComponent() {
        resetComponent();
    }

    public Vector2 getTilt() { return tilt; }

    public TiltComponent setTilt(final Vector2 tilt) {
        this.tilt.set(tilt.x, tilt.y);
        this.tilt.limit(MAX_TILT_VECTOR_LEN);

        if (!tilt.isZero()) {
            headingComponent.setHeadingFrom(tilt);
        }

        return this;
    }

    @Override
    public void initialize(final Entity owner) {
        headingComponent = owner.requireComponent(HeadingComponent.class);
    }

    @Override
    protected void resetComponent() {
        tilt.setZero();
        headingComponent = null;
    }
}
