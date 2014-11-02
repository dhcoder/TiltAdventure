package tiltadv.components.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import dhcoder.libgdx.entity.AbstractComponent;
import dhcoder.libgdx.entity.Entity;
import dhcoder.support.time.Duration;
import tiltadv.components.dynamics.TiltComponent;
import tiltadv.memory.Pools;

import static com.badlogic.gdx.Input.Keys;

/**
 * Component which handles the user using a keyboard to
 */
public final class KeyboardInputComponent extends AbstractComponent {
    private TiltComponent tiltComponent;

    @Override
    public void initialize(final Entity owner) {
        tiltComponent = owner.requireComponent(TiltComponent.class);
    }

    @Override
    public void update(final Duration elapsedTime) {
        Vector2 tilt = Pools.vector2s.grabNew();

        float tiltMagnitude = 2f;
        if (Gdx.input.isKeyPressed(Keys.SHIFT_LEFT)) {
            tiltMagnitude *= 0.25f;
        }

        if (Gdx.input.isKeyPressed(Keys.A)) { tilt.x = -tiltMagnitude; }
        else if (Gdx.input.isKeyPressed(Keys.D)) { tilt.x = tiltMagnitude; }

        if (Gdx.input.isKeyPressed(Keys.S)) { tilt.y = -tiltMagnitude; }
        else if (Gdx.input.isKeyPressed(Keys.W)) { tilt.y = tiltMagnitude; }

        tiltComponent.setTilt(tilt);

        Pools.vector2s.free(tilt);
    }

    @Override
    public void reset() {
        tiltComponent = null;
    }
}
