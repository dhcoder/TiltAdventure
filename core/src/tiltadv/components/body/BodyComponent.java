package tiltadv.components.body;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.physics.box2d.World;
import dhcoder.libgdx.entity.AbstractComponent;
import dhcoder.libgdx.entity.Entity;
import dhcoder.support.time.Duration;
import dhcoder.libgdx.physics.PhysicsSystem;
import tiltadv.globals.Services;
import tiltadv.memory.Pools;

/**
 * A component that encapsulates an entity's physical body - represented by a Box2D {@link Body}. This maintains the
 * knowledge of an entity's position, motion, and currently applied forces.
 */
public final class BodyComponent extends AbstractComponent {

    private BodyType bodyType;
    private Body body;
    private Shape shape;

    @Override
    public void update(final Duration elapsedTime) {
        super.update(elapsedTime);
    }

    private PositionComponent positionComponent;

    public BodyComponent() { reset(); }

    public BodyComponent setBodyType(final BodyType bodyType) {
        this.bodyType = bodyType;
        return this;
    }

    public BodyComponent setShape(final Shape shape) {
        this.shape = shape;
        return this;
    }

    @Override
    public void initialize(final Entity owner) {
        positionComponent = owner.requireComponent(PositionComponent.class);

        final World world = Services.get(PhysicsSystem.class).getWorld();

        {
            BodyDef bodyDef = Pools.bodyDefs.grabNew();
            bodyDef.type = bodyType;
            body = world.createBody(bodyDef);
            body.setUserData(this);
            Pools.bodyDefs.freeCount(1);
        }

        {
            FixtureDef fixtureDef = Pools.fixtureDefs.grabNew();
            fixtureDef.shape = shape;
            body.createFixture(fixtureDef);
            Pools.fixtureDefs.freeCount(1);
        }

    }


    @Override
    public void reset() {
        bodyType = BodyType.StaticBody;

        final World world = Services.get(PhysicsSystem.class).getWorld();
        world.destroyBody(body);
        body = null;
    }

    /**
     * After the physics step is run, bodies must be synced to update their position with our system.
     */
    public void sync() {
        positionComponent.setPosition(body.getPosition());
    }
}
