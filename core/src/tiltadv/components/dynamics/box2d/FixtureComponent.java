package tiltadv.components.dynamics.box2d;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Shape;
import dhcoder.libgdx.entity.AbstractComponent;
import dhcoder.libgdx.entity.Entity;
import dhcoder.libgdx.physics.PhysicsUpdateListener;
import dhcoder.libgdx.physics.PhysicsSystem;
import dhcoder.support.opt.Opt;
import tiltadv.components.dynamics.PositionComponent;
import tiltadv.globals.Physics;
import tiltadv.globals.Services;
import tiltadv.memory.Pools;

import static dhcoder.support.contract.ContractUtils.requireNonNull;
import static dhcoder.support.contract.ContractUtils.requireNull;
import static dhcoder.support.contract.ContractUtils.requireTrue;

/**
 * A component that encapsulates a Box2D {@link Fixture} which should be attached to a Box2D {@link Body}. You can
 * either specify the body explicitly, or else this fixture will assume there's a body component already attached to
 * this component's {@link Entity}
 * <p/>
 * If this entity has a fixture but no body, it will assume the entity exists simply to support a child fixture for
 * some other entity's body. If this is the case, this fixture expects the presence of a {@link PositionComponent}
 * and will take over the role of setting it.
 */
public final class FixtureComponent extends AbstractComponent implements PhysicsUpdateListener {

    private final Vector2 offset = new Vector2();
    // The position component will only be set if it's our responsibility to set it
    private final Opt<PositionComponent> positionComponentOpt = Opt.withNoValue();
    private Entity targetEntity;
    private Shape shape;
    private boolean isSensor;
    private short categoryBits = 0;
    private short maskBits = -1;
    private Fixture fixture;

    public FixtureComponent setTargetEntity(final Entity targetEntity) {
        this.targetEntity = targetEntity;
        return this;
    }

    public FixtureComponent setSensor(final boolean isSensor) {
        requireNull(fixture, "Can't change a fixture's sensor state after it is initialized");
        this.isSensor = isSensor;
        return this;
    }

    public FixtureComponent setShape(final Shape shape) {
        requireNull(fixture, "Can't change a fixture's shape after it is initialized");
        this.shape = shape;
        return this;
    }

    public FixtureComponent setOffset(final Vector2 offset) {
        requireNull(fixture, "Can't change a fixture's offset after it is initialized");
        this.offset.set(offset);
        return this;
    }

    public FixtureComponent setCategory(final int category) {
        requireNull(fixture, "Can't change a fixture's filter settings after it is initialized");
        this.categoryBits = (short)category;
        this.maskBits = (short)Services.get(PhysicsSystem.class).getCategoryMask(category);
        return this;
    }

    @Override
    public void initialize(final Entity owner) {
        requireNonNull(shape, "Fixture shape must be set");
        requireTrue(categoryBits > 0, "Fixture category must be set");

        if (targetEntity == null) {
            targetEntity = owner;
        }

        BodyComponent bodyComponent = targetEntity.requireComponent(BodyComponent.class);
        if (targetEntity != owner) {
            positionComponentOpt.set(owner.requireComponent(PositionComponent.class));
            // Only sync if it's our responsibility to maintain the position
            Services.get(PhysicsSystem.class).addUpdateListener(this);
        }

        {
            FixtureDef fixtureDef = Pools.fixtureDefs.grabNew();
            fixtureDef.shape = shape;
            fixtureDef.isSensor = isSensor;
            fixtureDef.friction = 0f;
            fixtureDef.density = isSensor ? 0f : 1f;
            fixtureDef.filter.categoryBits = categoryBits;
            fixtureDef.filter.maskBits = maskBits;
            final Body body = bodyComponent.getBody();
            fixture = body.createFixture(fixtureDef);
            Pools.fixtureDefs.freeCount(1);
        }
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

    @Override
    public void reset() {
        if (positionComponentOpt.hasValue()) {
            Services.get(PhysicsSystem.class).removeUpdateListener(this);
        }

        offset.setZero();
        positionComponentOpt.clear();
        targetEntity = null;
        shape = null;
        isSensor = false;
        categoryBits = 0;
        maskBits = -1;

        fixture = null;
    }
}
