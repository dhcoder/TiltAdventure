package tiltadv.components.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import dhcoder.libgdx.entity.AbstractComponent;
import dhcoder.libgdx.entity.Entity;
import dhcoder.support.time.Duration;
import tiltadv.components.model.TiltComponent;
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

        if (Gdx.input.isKeyPressed(Keys.DPAD_LEFT)) { tilt.x = -2f; }
        else if (Gdx.input.isKeyPressed(Keys.DPAD_RIGHT)) { tilt.x = 2f; }

        if (Gdx.input.isKeyPressed(Keys.DPAD_DOWN)) { tilt.y = -2f; }
        else if (Gdx.input.isKeyPressed(Keys.DPAD_UP)) { tilt.y = 2f; }

        tiltComponent.setTilt(tilt);

        Pools.vector2s.free(tilt);
    }

    @Override
    protected void resetComponent() {
        tiltComponent = null;
    }
}
