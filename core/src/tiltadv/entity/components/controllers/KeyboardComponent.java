package tiltadv.entity.components.controllers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import dhcoder.support.immutable.ImmutableDuration;
import tiltadv.entity.AbstractComponent;
import tiltadv.entity.Entity;
import tiltadv.entity.components.data.TiltComponent;
import tiltadv.immutable.ImmutableVector2;

import static com.badlogic.gdx.Input.Keys;

/**
 * Component which handles the user using a keyboard to
 */
public class KeyboardComponent extends AbstractComponent {

    private final Vector2 tiltVector = new Vector2();
    private final ImmutableVector2 immutableTiltVector = new ImmutableVector2(tiltVector);
    private TiltComponent tiltComponent;

    @Override
    public void initialize(final Entity owner) {
        tiltComponent = owner.requireComponent(TiltComponent.class);
    }

    @Override
    public void update(final ImmutableDuration elapsedTime) {

        tiltVector.setZero();

        if (Gdx.input.isKeyPressed(Keys.DPAD_LEFT)) { tiltVector.x = -2f; }
        else if (Gdx.input.isKeyPressed(Keys.DPAD_RIGHT)) { tiltVector.x = 2f; }

        if (Gdx.input.isKeyPressed(Keys.DPAD_DOWN)) { tiltVector.y = -2f; }
        else if (Gdx.input.isKeyPressed(Keys.DPAD_UP)) { tiltVector.y = 2f; }

        tiltComponent.setTilt(immutableTiltVector);
    }
}
