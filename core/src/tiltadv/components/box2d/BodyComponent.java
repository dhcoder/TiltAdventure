package tiltadv.components.box2d;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.physics.box2d.World;
import dhcoder.libgdx.entity.Entity;
import dhcoder.libgdx.physics.PhysicsSystem;
import dhcoder.support.math.Angle;
import dhcoder.support.time.Duration;
import tiltadv.components.body.PositionComponent;
import tiltadv.globals.Physics;
import tiltadv.globals.Services;
import tiltadv.memory.Pools;

import static dhcoder.support.contract.ContractUtils.requireNonNull;

/**
 * A component that encapsulates an entity's physical body - represented by a Box2D {@link Body}. This maintains the
 * knowledge of an entity's position, motion, and heading.
 */
public final class BodyComponent extends PositionComponent {

    private final Vector2 desiredPosition = new Vector2();
    private final Vector2 gameVelocity = new Vector2(); // Velocity in units of pixels per second
    private final Angle heading = Angle.fromRadians(0f);
    private BodyType bodyType = BodyType.StaticBody;
    private Body body;
    private Shape shape;
    private boolean isFastMoving;
    private boolean isDesiredPositionSet;
    private int headingLockedCount;

    @Override
    protected void handleConstruction() {
        super.handleConstruction();
    }

    public BodyComponent setBodyType(final BodyType bodyType) {
        this.bodyType = bodyType;
        return this;
    }

    public BodyComponent setShape(final Shape shape) {
        this.shape = shape;
        return this;
    }

    public BodyComponent setFastMoving(final boolean isFastMoving) {
        this.isFastMoving = isFastMoving;
        return this;
    }

    /**
     * Set the position we want to try to get to by the next frame. That position may be impossible because physics will
     * prevent this entity from getting there, at which point, this object should move as best as it can based on the
     * scene's constraints.
     */
    public BodyComponent setPosition(final Vector2 position) {
        desiredPosition.set(position).scl(Physics.PIXELS_TO_METERS);
        isDesiredPositionSet = true;
        return this;
    }

    public BodyComponent setVelocity(final Vector2 velocity) {
        Vector2 physicsVelocity = Pools.vector2s.grabNew();
        physicsVelocity.set(velocity).scl(Physics.PIXELS_TO_METERS);
        body.setLinearVelocity(velocity);
        Pools.vector2s.freeCount(1);

        return this;
    }

    public BodyComponent setHeading(final Angle heading) {
        if (headingLockedCount > 0) {
            return this;
        }

        body.setTransform(body.getPosition(), heading.getRadians());
        return this;
    }

    public BodyComponent setHeadingFrom(final Vector2 target) {
        if (headingLockedCount > 0) {
            return this;
        }

        Angle heading = Pools.angles.grabNew();
        heading.setRadians(target.angleRad());
        setHeading(heading);
        Pools.angles.freeCount(1);

        return this;
    }

    public Angle getHeading() {
        heading.setRadians(body.getAngle());
        return heading;
    }

    public void lockHeading(final boolean locked) {
        headingLockedCount += (locked ? 1 : -1);
    }

    public BodyComponent applyImpulse(final Vector2 impulse) {
        Vector2 physicsImpulse = Pools.vector2s.grabNew();
        Vector2 bodyCenter = Pools.vector2s.grabNew();
        physicsImpulse.set(impulse).scl(Physics.PIXELS_TO_METERS);
        body.applyLinearImpulse(physicsImpulse, bodyCenter, true);
        Pools.vector2s.freeCount(2);

        return this;
    }

    public Vector2 getVelocity() {
        gameVelocity.set(body.getLinearVelocity()).scl(Physics.METERS_TO_PIXELS);
        return gameVelocity;
    }

    @Override
    protected void handleUpdate(final Duration elapsedTime) {

        if (isDesiredPositionSet) {
            final int mark = Pools.vector2s.mark();
            Vector2 velocity = Pools.vector2s.grabNew();
            Vector2 deltaPosition = Pools.vector2s.grabNew();

            deltaPosition.set(desiredPosition).sub(body.getPosition());
            // We want to go this distance over the elapsed time... velocity = distance / time
            velocity.set(deltaPosition).scl(1 / elapsedTime.getSeconds());
            body.setLinearVelocity(velocity);
            isDesiredPositionSet = false;

            Pools.vector2s.freeToMark(mark);
        }

        if (getVelocity().isZero(0.1f)) {
            Vector2 velocity = Pools.vector2s.grabNew();
            setVelocity(velocity);
            Pools.vector2s.freeCount(1);
        }
    }

    @Override
    public void handleInitialize(final Entity owner) {
        requireNonNull(shape, "Body shape must be set");

        {
            final World world = Services.get(PhysicsSystem.class).getWorld();
            BodyDef bodyDef = Pools.bodyDefs.grabNew();
            bodyDef.type = bodyType;
            bodyDef.bullet = isFastMoving;
            bodyDef.fixedRotation = true;
            if (isDesiredPositionSet) {
                bodyDef.position.set(desiredPosition);
                isDesiredPositionSet = false;
            }
            bodyDef.linearDamping = 10f;
            body = world.createBody(bodyDef);
            body.setUserData(this);
            body.getAngle();
            Pools.bodyDefs.freeCount(1);
        }

        {
            FixtureDef fixtureDef = Pools.fixtureDefs.grabNew();
            fixtureDef.shape = shape;
            body.createFixture(fixtureDef);
            Pools.fixtureDefs.freeCount(1);
            shape = null;
        }
    }

    @Override
    public void handleReset() {
        desiredPosition.setZero();
        gameVelocity.setZero();
        heading.setRadians(0f);
        final World world = Services.get(PhysicsSystem.class).getWorld();
        world.destroyBody(body);
        body = null;
        shape = null;

        bodyType = BodyType.StaticBody;
        isFastMoving = false;
        isDesiredPositionSet = false;
        headingLockedCount = 0;
    }

    /**
     * Sync this component's physics location to an on-screen location.
     */
    public void sync() {
        Vector2 worldPosition = Pools.vector2s.grabNew();
        worldPosition.set(body.getPosition()).scl(Physics.METERS_TO_PIXELS);
        setPositionInternal(worldPosition);
        Pools.vector2s.freeCount(1);
    }
}