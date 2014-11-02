package tiltadv.components.box2d;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Shape;
import dhcoder.libgdx.entity.AbstractComponent;
import dhcoder.libgdx.entity.Entity;
import dhcoder.libgdx.physics.PhysicsElement;
import dhcoder.libgdx.physics.PhysicsSystem;
import dhcoder.support.opt.Opt;
import tiltadv.components.dynamics.PositionComponent;
import tiltadv.globals.Physics;
import tiltadv.globals.Services;
import tiltadv.memory.Pools;

import static dhcoder.support.contract.ContractUtils.requireNonNull;

/**
 * A component that encapsulates a Box2D {@link Fixture} which should be attached to a Box2D {@link Body}. You can
 * either specify the body explicitly, or else this fixture will assume there's a body component already attached to
 * this component's {@link Entity}
 * <p/>
 * If this entity has a fixture but no body, it will assume the entity exists simply to support a child fixture for
 * some other entity's body. If this is the case, this fixture expects the presence of a {@link PositionComponent}
 * and will take over the role of setting it.
 */
public final class FixtureComponent extends AbstractComponent implements PhysicsElement {

    private final Vector2 offset = new Vector2();
    // The position component will only be set if it's our responsibility to set it
    private final Opt<PositionComponent> positionComponentOpt = Opt.withNoValue();
    private Entity targetEntity;
    private Shape shape;
    private boolean isSensor;
    private boolean isInitialized;
    private Fixture fixture;

    public FixtureComponent setTargetEntity(final Entity targetEntity) {
        this.targetEntity = targetEntity;
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

    public FixtureComponent setOffset(final Vector2 offset) {
        this.offset.set(offset);
        return this;
    }

    @Override
    public void initialize(final Entity owner) {
        requireNonNull(shape, "Body shape must be set");

        if (targetEntity == null) {
            targetEntity = owner;
        }

        BodyComponent bodyComponent = targetEntity.requireComponent(BodyComponent.class);
        if (targetEntity != owner) {
            positionComponentOpt.set(owner.requireComponent(PositionComponent.class));
            // Only sync if it's our responsibility to maintain the position
            Services.get(PhysicsSystem.class).addElement(this);
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
        }

        isInitialized = true;
    }

    @Override
    public void reset() {
        if (positionComponentOpt.hasValue()) {
            Services.get(PhysicsSystem.class).removeElement(this);
        }

        offset.setZero();
        positionComponentOpt.clear();
        targetEntity = null;
        shape = null;
        isSensor = false;
        isInitialized = false;
        fixture = null;
    }

    @Override
    public void syncWithPhysics() {
        final Body body = fixture.getBody();

        Vector2 transformedOffset = Pools.vector2s.grabNew();
        transformedOffset.set(offset).scl(Physics.PIXELS_TO_METERS);
        body.getTransform().mul(transformedOffset);
        transformedOffset.scl(Physics.METERS_TO_PIXELS);

        positionComponentOpt.getValue().setPosition(transformedOffset);

        Pools.vector2s.freeCount(1);

    }
}
