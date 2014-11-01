package tiltadv.components.body;

import com.badlogic.gdx.math.Vector2;

/**
 * A {@link PositionComponent} that can simply be set.
 */
public final class SimplePositionComponent extends PositionComponent {

    private final Vector2 position = new Vector2();

    @Override
    public Vector2 getPosition() {
        return position;
    }

    @Override
    public SimplePositionComponent setPosition(final Vector2 position) {
        this.position.set(position);
        return this;
    }

    @Override
    protected void handleReset() {
        position.setZero();
    }
}

