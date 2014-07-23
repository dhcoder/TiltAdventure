package tiltadv.entity.components.controllers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import dhcoder.support.time.Duration;
import tiltadv.entity.AbstractComponent;
import tiltadv.entity.Entity;
import tiltadv.entity.components.data.TiltComponent;

import static com.badlogic.gdx.Input.Keys;

/**
 * Component which handles the user using a keyboard to
 */
public final class KeyboardComponent extends AbstractComponent {

    private final Vector2 tilt = new Vector2(); // TODO: Replace with pool
    private TiltComponent tiltComponent;

    @Override
    public void initialize(final Entity owner) {
        tiltComponent = owner.requireComponent(TiltComponent.class);
    }

    @Override
    public void update(final Duration elapsedTime) {

        tilt.setZero();

        if (Gdx.input.isKeyPressed(Keys.DPAD_LEFT)) { tilt.x = -2f; }
        else if (Gdx.input.isKeyPressed(Keys.DPAD_RIGHT)) { tilt.x = 2f; }

        if (Gdx.input.isKeyPressed(Keys.DPAD_DOWN)) { tilt.y = -2f; }
        else if (Gdx.input.isKeyPressed(Keys.DPAD_UP)) { tilt.y = 2f; }

        tiltComponent.setTilt(tilt);
    }
}
