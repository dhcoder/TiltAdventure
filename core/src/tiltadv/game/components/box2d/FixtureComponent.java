package tiltadv.game.components.box2d;

import com.badlogic.gdx.physics.box2d.FixtureDef;
import tiltadv.game.Entity;
import tiltadv.game.components.Component;

/**
 * A component that wraps a Box2D fixture.
 */
public final class FixtureComponent implements Component {

    private final FixtureDef fixtureDef;

    public FixtureComponent(final FixtureDef fixtureDef) {
        this.fixtureDef = fixtureDef;
    }

    public FixtureDef getFixtureDef() {
        return fixtureDef;
    }

    @Override
    public void initialize(final Entity owner) { }

    @Override
    public void dispose() { }
}
