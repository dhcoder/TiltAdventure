package tiltadv.game.components.box2d;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.World;
import tiltadv.game.Entity;
import tiltadv.game.components.SingletonComponent;

/**
 * A component that wraps a Box2D body.
 * <p/>
 * A body component expects {@link FixtureComponents} to be present or else it will throw an {@link
 * IllegalStateException} at initialization time.
 */
public final class BodyComponent implements SingletonComponent {

    private final World world;
    private Body body;

    public BodyComponent(final World world) {
        this.world = world;
    }

    @Override
    public void initialize(final Entity owner) {
        FixtureComponents fixtureComponents = owner.getComponent(FixtureComponents.class).value();

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        body = world.createBody(bodyDef);

        for (FixtureComponent component : fixtureComponents.getComponents()) {
            body.createFixture(component.getFixtureDef());
        }
    }

    @Override
    public void dispose() {
        world.destroyBody(body);
    }
}
