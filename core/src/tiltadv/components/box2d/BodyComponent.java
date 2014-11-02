package tiltadv.components.box2d;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.joints.DistanceJointDef;
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
public final class BodyComponent extends PositionComponent<BodyComponent> {

    /**
     * Box2D objects take too long to come to rest, so just manually stop them ourselves past a certain epsilon
     */
    private static final float STOP_EPSILON = 10f;
    private final Vector2 gamePosition = new Vector2();
    private final Vector2 gameVelocity = new Vector2(); // Velocity in units of pixels per second
    private final Angle heading = Angle.fromRadians(0f);
    private BodyType bodyType = BodyType.StaticBody;
    private Body body;
    private Shape shape;
    private boolean isFastMoving;
    private boolean syncPosition;
    private int headingLockedCount;
    private boolean isSensor;

    public BodyComponent setSensor(final boolean isSensor) {
        this.isSensor = isSensor;
        return this;
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
    @Override
    public BodyComponent setPosition(final Vector2 position) {
        gamePosition.set(position);
        syncPosition = true;
        return this;
    }

    @Override
    public Vector2 getPosition() {
        return gamePosition;
    }

    public Body getBody() {
        return body;
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

    public void joinTo(final BodyComponent target) {
        if (target == this) {
            throw new IllegalArgumentException("Can't join a body to itself");
        }

//        RevoluteJointDef revoluteJointDef = Pools.revoluteJointDefs.grabNew();
//        revoluteJointDef.bodyA = this.body;
//        revoluteJointDef.bodyB = target.body;
//        revoluteJointDef.localAnchorA.set(this.body.getPosition());
//        revoluteJointDef.localAnchorB.set(target.getPosition()).scl(-1f);
//        revoluteJointDef.enableMotor = true;
//        revoluteJointDef.motorSpeed = 50;

        DistanceJointDef distanceJointDefs = Pools.distanceJointDefs.grabNew();
        distanceJointDefs.bodyA = this.body;
        distanceJointDefs.bodyB = target.body;
        distanceJointDefs.localAnchorA.set(this.body.getPosition());
        distanceJointDefs.localAnchorB.set(target.getPosition()).scl(-1f);

        final World world = Services.get(PhysicsSystem.class).getWorld();
//        world.createJoint(revoluteJointDef);
        world.createJoint(distanceJointDefs);
//        Pools.revoluteJointDefs.freeCount(1);
        Pools.distanceJointDefs.freeCount(1);
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

        if (syncPosition) {
            final int mark = Pools.vector2s.mark();
            Vector2 velocity = Pools.vector2s.grabNew();
            Vector2 deltaPosition = Pools.vector2s.grabNew();

            deltaPosition.set(gamePosition).scl(Physics.PIXELS_TO_METERS).sub(body.getPosition());
            // We want to go this distance over the elapsed time... velocity = distance / time
            velocity.set(deltaPosition).scl(1 / elapsedTime.getSeconds());
            body.setLinearVelocity(velocity);
            syncPosition = false;

            Pools.vector2s.freeToMark(mark);
        }

        if (getVelocity().isZero(STOP_EPSILON)) {
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
            if (syncPosition) {
                bodyDef.position.set(gamePosition).scl(Physics.PIXELS_TO_METERS);
                syncPosition = false;
            }
            bodyDef.linearDamping = 10f;
            body = world.createBody(bodyDef);
            body.setUserData(this);
            Pools.bodyDefs.freeCount(1);
        }

        {
            FixtureDef fixtureDef = Pools.fixtureDefs.grabNew();
            fixtureDef.shape = shape;
            fixtureDef.isSensor = isSensor;
            fixtureDef.friction = 0f;
            fixtureDef.density = 0f;
            body.createFixture(fixtureDef);
            Pools.fixtureDefs.freeCount(1);
            shape = null;
        }
    }

    @Override
    public void handleReset() {
        gamePosition.setZero();
        gameVelocity.setZero();
        heading.setRadians(0f);
        final World world = Services.get(PhysicsSystem.class).getWorld();
        world.destroyBody(body);
        body = null;
        shape = null;

        bodyType = BodyType.StaticBody;
        isSensor = false;
        isFastMoving = false;
        syncPosition = false;
        headingLockedCount = 0;
    }

    /**
     * Sync this component's physics location to an on-screen location.
     */
    public void sync() {
        gamePosition.set(body.getPosition()).scl(Physics.METERS_TO_PIXELS);
    }
}
