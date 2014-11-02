package tiltadv.components.box2d;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Shape;
import dhcoder.libgdx.entity.Entity;
import tiltadv.components.body.PositionComponent;
import tiltadv.memory.Pools;

import static dhcoder.support.contract.ContractUtils.requireNonNull;

/**
 * A component that encapsulates an entity's physical body - represented by a Box2D {@link Body}. This maintains the
 * knowledge of an entity's position, motion, and heading.
 */
public final class FixtureComponent extends PositionComponent<FixtureComponent> {

    private final Vector2 gamePosition = new Vector2();
    private BodyComponent bodyComponent;
    private Shape shape;
    private boolean isSensor;
    private boolean isInitialized;
    private Fixture fixture;

    public FixtureComponent setBodyComponent(final BodyComponent bodyComponent) {
        this.bodyComponent = bodyComponent;
        return this;
    }

    public FixtureComponent setSensor(final boolean isSensor) {
        this.isSensor = isSensor;
        return this;
    }

    public FixtureComponent setShape(final Shape shape) {
        this.shape = shape;
        return this;
    }

    /**
     * Set the position we want to try to get to by the next frame. That position may be impossible because physics will
     * prevent this entity from getting there, at which point, this object should move as best as it can based on the
     * scene's constraints.
     */
    @Override
    public FixtureComponent setPosition(final Vector2 position) {
        if (isInitialized) {
            throw new IllegalStateException("Can't set a fixture's position post initialization.");
        }
        gamePosition.set(position);
        return this;
    }

    @Override
    public Vector2 getPosition() {
//        gamePosition.set(fixture.getShape().get)
        return gamePosition;
    }

    @Override
    public void handleInitialize(final Entity owner) {
        requireNonNull(shape, "Body shape must be set");

        if (bodyComponent == null) {
            bodyComponent = owner.requireComponent(BodyComponent.class);
        }

        {
            FixtureDef fixtureDef = Pools.fixtureDefs.grabNew();
            fixtureDef.shape = shape;
            fixtureDef.isSensor = isSensor;
            fixtureDef.friction = 0f;
            fixtureDef.density = 0f;

            final Body body = bodyComponent.getBody();
            fixture = body.createFixture(fixtureDef);
            Pools.fixtureDefs.freeCount(1);
            shape = null;
            bodyComponent = null;
        }

        isInitialized = true;
    }

    @Override
    public void handleReset() {
        bodyComponent = null;

        gamePosition.setZero();
        shape = null;

        isSensor = false;
        isInitialized = false;

        fixture = null;
    }
}
