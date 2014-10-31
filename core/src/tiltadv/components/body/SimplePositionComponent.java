package tiltadv.components.body;

import com.badlogic.gdx.math.Vector2;

/**
 * A {@link PositionComponent} that can simply be set.
 */
public final class SimplePositionComponent extends PositionComponent {
    public SimplePositionComponent setPosition(final Vector2 position) {
        return (SimplePositionComponent)setPositionInternal(position);
    }

}
