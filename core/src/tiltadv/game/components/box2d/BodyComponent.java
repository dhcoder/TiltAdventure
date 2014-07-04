package tiltadv.game.components.box2d;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.World;
import tiltadv.game.Entity;
import tiltadv.game.components.Component;

import java.util.List;

import static tiltadv.util.ComponentUtils.requireComponents;
import static tiltadv.util.ComponentUtils.requireSingleInstance;

/**
 * A component that wraps a Box2D body.
 * <p/>
 * A body component expects at least one {@link FixtureComponent} to be present or else it will throw an {@link
 * IllegalStateException} at initialization time.
 */
public final class BodyComponent implements Component {

    private final World world;
    private Body body;

    public BodyComponent(final World world) {
        this.world = world;
    }

    @Override
    public void initialize(final Entity owner) {

        requireSingleInstance(owner, BodyComponent.class);

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        body = world.createBody(bodyDef);

        List<FixtureComponent> fixtureComponents =
            (List<FixtureComponent>)requireComponents(owner, FixtureComponent.class);

        for (FixtureComponent component : fixtureComponents) {
            body.createFixture(component.getFixtureDef());
        }
    }

    @Override
    public void dispose() {
        world.destroyBody(body);
    }
}
