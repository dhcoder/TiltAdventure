package tiltadv.components.box2d;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import dhcoder.libgdx.entity.Entity;
import tiltadv.components.body.PositionComponent;
import tiltadv.globals.Physics;
import tiltadv.memory.Pools;

import static dhcoder.support.contract.ContractUtils.requireNonNull;

/**
 * A component that encapsulates a Box2D {@link Fixture} which should be attached to a Box2D {@link Body}. You can
 * either specify the body explicitly, or else this fixture will assume there's a body component already attached to
 * this component's {@link Entity}
 */
public final class CircleFixtureComponent extends PositionComponent<CircleFixtureComponent> {

    private final Vector2 physicsPosition = new Vector2();
    private final Vector2 gamePosition = new Vector2();
    private BodyComponent bodyComponent;
    private CircleShape circleShape;
    private boolean isSensor;
    private boolean isInitialized;
    private Fixture fixture;

    public CircleFixtureComponent setBodyComponent(final BodyComponent bodyComponent) {
        this.bodyComponent = bodyComponent;
        return this;
    }

    public CircleFixtureComponent setSensor(final boolean isSensor) {
        this.isSensor = isSensor;
        return this;
    }

    public CircleFixtureComponent setShape(final CircleShape shape) {
        this.physicsPosition.set(shape.getPosition());
        this.circleShape = shape;
        return this;
    }

    /**
     * Set the position we want to try to get to by the next frame. That position may be impossible because physics will
     * prevent this entity from getting there, at which point, this object should move as best as it can based on the
     * scene's constraints.
     */
    @Override
    public CircleFixtureComponent setPosition(final Vector2 position) {
        if (isInitialized) {
            throw new IllegalStateException("Can't set a fixture's position directly; position the shape instead.");
        }
        return this;
    }

    @Override
    public Vector2 getPosition() {
        gamePosition.set(physicsPosition);
        fixture.getBody().getTransform().mul(gamePosition).scl(Physics.METERS_TO_PIXELS);
        return gamePosition;
    }

    @Override
    public void handleInitialize(final Entity owner) {
        requireNonNull(circleShape, "Body shape must be set");

        if (bodyComponent == null) {
            bodyComponent = owner.requireComponent(BodyComponent.class);
        }

        {
            FixtureDef fixtureDef = Pools.fixtureDefs.grabNew();
            fixtureDef.shape = circleShape;
            fixtureDef.isSensor = isSensor;
            fixtureDef.friction = 0f;
            fixtureDef.density = 0f;

            final Body body = bodyComponent.getBody();
            fixture = body.createFixture(fixtureDef);
            Pools.fixtureDefs.freeCount(1);
            circleShape = null;
            bodyComponent = null;
        }

        isInitialized = true;
    }

    @Override
    public void handleReset() {
        bodyComponent = null;

        physicsPosition.setZero();
        gamePosition.setZero();
        circleShape = null;

        isSensor = false;
        isInitialized = false;

        fixture = null;
    }
}
