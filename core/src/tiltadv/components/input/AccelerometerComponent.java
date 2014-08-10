package tiltadv.components.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import dhcoder.libgdx.entity.AbstractComponent;
import dhcoder.libgdx.entity.Entity;
import dhcoder.support.event.EventListener;
import dhcoder.support.time.Duration;
import tiltadv.globals.Events;
import tiltadv.memory.Pools;

import static dhcoder.support.text.StringUtils.format;

/**
 * Component which reads the device's accelerometer and sets the entity's tilt value accordingly.
 */
public final class AccelerometerComponent extends AbstractComponent {

    // If the tilt vector is smaller than the following value, we consider the amount of tilt too weak to count, and
    // instead just treat it as no tilt at all
    private static final float TILT_THRESHOLD = 0.2f;
    private final Vector3 accelerometerSnapshot = new Vector3();
    private boolean isTouchDown;
    private TiltComponent tiltComponent;
    private Duration printDuration = Duration.zero();

    @Override
    public void initialize(final Entity owner) {
        tiltComponent = owner.requireComponent(TiltComponent.class);
        Events.onScreenTouchDown.addListener(new EventListener() {
            @Override
            public void run(final Object sender) {
                accelerometerSnapshot
                    .set(Gdx.input.getAccelerometerX(), Gdx.input.getAccelerometerY(), Gdx.input.getAccelerometerZ());
                isTouchDown = true;
            }
        });
        Events.onScreenTouchUp.addListener(new EventListener() {
            @Override
            public void run(final Object sender) {
                isTouchDown = false;
            }
        });
    }

    public void update(final Duration elapsedTime) {

        boolean showMessage = false;
        printDuration.add(elapsedTime);
        if (printDuration.getSeconds() > 1f) {
            showMessage = true;
            printDuration.setZero();
        }

        Vector2 tilt = Pools.vector2s.grabNew();

        if (isTouchDown) {
            Vector3 accelerometerDiff = Pools.vector3s.grabNew();
            accelerometerDiff
                .set(Gdx.input.getAccelerometerX(), Gdx.input.getAccelerometerY(), Gdx.input.getAccelerometerZ())
                .sub(accelerometerSnapshot);

            if (showMessage) {
                Gdx.app.log("ACCEL", format("{0}x{1}x{2}", Gdx.input.getAccelerometerX(), Gdx.input.getAccelerometerY(),
                    Gdx.input.getAccelerometerZ()));
            }
            // Convert portrait accelerometer directions to landscape
            // See https://github.com/libgdx/libgdx/wiki/Accelerometer
            tilt.set(accelerometerDiff.y, -accelerometerDiff.x);
            Pools.vector3s.free(accelerometerDiff);

            if (tilt.isZero(TILT_THRESHOLD)) {
                tilt.setZero();
            }
        }
        tiltComponent.setTilt(tilt);
        Pools.vector2s.free(tilt);
    }
}
