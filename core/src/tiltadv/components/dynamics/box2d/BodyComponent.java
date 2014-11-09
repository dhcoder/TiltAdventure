package tiltadv.components.dynamics.box2d;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import dhcoder.libgdx.entity.AbstractComponent;
import dhcoder.libgdx.entity.Entity;
import dhcoder.libgdx.physics.PhysicsSystem;
import dhcoder.libgdx.physics.PhysicsUpdateListener;
import dhcoder.support.math.Angle;
import dhcoder.support.time.Duration;
import tiltadv.components.dynamics.PositionComponent;
import tiltadv.globals.Physics;
import tiltadv.globals.Services;
import tiltadv.memory.Pools;

import static dhcoder.support.contract.ContractUtils.requireNonNull;
import static dhcoder.support.contract.ContractUtils.requireNull;

/**
 * A component that encapsulates an entity's physical body - represented by a Box2D {@link Body}. This maintains the
 * knowledge of an entity's position, motion, and heading.
 * <p/>
 * If a body is present on an entity, it expects the presence of a {@link PositionComponent} and will take over the role
 * of setting it.
 */
public final class BodyComponent extends AbstractComponent implements PhysicsUpdateListener {

    public static final Duration IMPULSE_RECOVERY_TIME = Duration.fromMilliseconds(300f);
    /**
     * Box2D objects take too long to come to rest, so just manually stop them ourselves past a certain epsilon
     */
    private static final float STOP_EPSILON = .01f; // How slow are moving in meters per second
    public static boolean RUN_SANITY_CHECKS = false;
    private final Vector2 targetPosition = new Vector2(); // Position in meters
    private final Vector2 targetVelocity = new Vector2(); // If set, apply a constant impulse to stay at this velocity
    private final Angle heading = Angle.fromRadians(0f);
    private final Duration velocityLockedDuration = Duration.zero();
    private BodyType bodyType = BodyType.StaticBody;
    private Body body;
    private boolean isFastMoving;
    private float initialDamping = Physics.DAMPING_MEDIUM_STOP;
    private boolean syncPosition;
    private int headingLockedCount;
    private PositionComponent positionComponent;

    public BodyComponent setBodyType(final BodyType bodyType) {
        requireNull(body, "Can't set body type after entity is initialized");

        this.bodyType = bodyType;
        return this;
    }

    /**
     * Set a linear damping value, if you want the object to gradually come to a stop over time. By default, it's set so
     * a body will stop relatively quickly, but you should set it to {@ocde 0f} if you want the object to go forever.
     * <p/>
     * See the {@link Physics} namespace for useful values.
     */
    public BodyComponent setDamping(final float damping) {
        if (body == null) {
            initialDamping = damping;
        }
        else {
            body.setLinearDamping(damping);
        }

        return this;
    }

    /**
     * Set this to {@oode true} if you think this object will move so fast that the physics system should do more
     * intensive calculations to make sure it doesn't pass through walls.
     */
    public BodyComponent setFastMoving(final boolean isFastMoving) {
        requireNull(body, "Can't set body to fast moving after entity is initialized");

        this.isFastMoving = isFastMoving;
        return this;
    }

    public BodyComponent setVelocity(final Vector2 velocity) {
        if (velocityLockedDuration.getSeconds() > 0) {
            return this;
        }

        Physics.toMeters(targetVelocity.set(velocity));
        if (body != null) {
            assertNonStaticType();
            targetVelocity.scl(body.getMass());
        }

        return this;
    }

    public BodyComponent stopSmoothly() {
        if (velocityLockedDuration.getSeconds() > 0) {
            return this;
        }
        targetVelocity.setZero();
        return this;
    }

    /**
     * Set this body's velocity to a value that will try and get this body to a certain position on the next frame.
     * NOTE: This position may be impossible because physics can prevent this entity from getting there, at which point,
     * this object should move as best as it can based on the scene's constraints.
     */
    public BodyComponent setDesiredPosition(final Vector2 position) {
        targetPosition.set(position).scl(Physics.PIXELS_TO_METERS);
        syncPosition = true;
        return this;
    }

    public Body getBody() {
        requireNonNull(body, "Can't request a body before it is initialized.");
        return body;
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

    public BodyComponent setHeading(final Angle heading) {
        if (headingLockedCount > 0) {
            return this;
        }

        body.setTransform(body.getPosition(), heading.getRadians());
        return this;
    }

    public void lockHeading(final boolean locked) {
        headingLockedCount += (locked ? 1 : -1);
    }

    public BodyComponent applyImpulse(final Vector2 impulse) {
        targetVelocity.setZero();
        velocityLockedDuration.setFrom(IMPULSE_RECOVERY_TIME);

        Vector2 physicsImpulse = Pools.vector2s.grabNew();
        physicsImpulse.set(impulse).scl(Physics.PIXELS_TO_METERS);
        body.applyLinearImpulse(physicsImpulse, body.getWorldCenter(), true);
        Pools.vector2s.freeCount(1);

        return this;
    }

    @Override
    public void initialize(final Entity owner) {

        positionComponent = owner.requireComponent(PositionComponent.class);

        final PhysicsSystem physicsSystem = Services.get(PhysicsSystem.class);

        {
            BodyDef bodyDef = Pools.bodyDefs.grabNew();
            bodyDef.type = bodyType;
            bodyDef.bullet = isFastMoving;
            if (!targetVelocity.isZero()) {
                bodyDef.linearVelocity.set(targetVelocity);
            }

            if (syncPosition) {
                bodyDef.position.set(targetPosition);
                syncPosition = false;
            }
            else {
                bodyDef.position.set(positionComponent.getPosition()).scl(Physics.PIXELS_TO_METERS);
            }
            bodyDef.linearDamping = initialDamping;
            body = physicsSystem.getWorld().createBody(bodyDef);
            body.setUserData(owner);
            Pools.bodyDefs.freeCount(1);
        }

        physicsSystem.addUpdateListener(this);
    }

    @Override
    public void update(final Duration elapsedTime) {

        if (RUN_SANITY_CHECKS) {
            if (body.getFixtureList().size == 0) {
                throw new IllegalStateException("Invalid body created without at least one fixture");
            }
        }

        velocityLockedDuration.subtract(elapsedTime);

        if (syncPosition) {
            final int mark = Pools.vector2s.mark();
            Vector2 velocity = Pools.vector2s.grabNew();
            Vector2 deltaPosition = Pools.vector2s.grabNew();

            deltaPosition.set(targetPosition).sub(body.getPosition());
            // We want to go this distance over the elapsed time... velocity = distance / time
            velocity.set(deltaPosition).scl(1 / elapsedTime.getSeconds());
            body.setLinearVelocity(velocity);
            syncPosition = false;

            Pools.vector2s.freeToMark(mark);
        }
        else if (!targetVelocity.isZero()) {
            Vector2 targetVelocity = Pools.vector2s.grabNew().set(this.targetVelocity);
            targetVelocity.sub(body.getLinearVelocity());
            body.applyLinearImpulse(targetVelocity, body.getWorldCenter(), true);
            Pools.vector2s.freeCount(1);
        }

        if (!body.getLinearVelocity().isZero() && body.getLinearVelocity().isZero(STOP_EPSILON)) {
            targetVelocity.setZero();
            body.setLinearVelocity(targetVelocity);
        }
    }

    @Override
    public void reset() {
        positionComponent = null;

        velocityLockedDuration.setZero();
        targetPosition.setZero();
        targetVelocity.setZero();
        heading.setRadians(0f);

        final PhysicsSystem physicsSystem = Services.get(PhysicsSystem.class);
        physicsSystem.destroyBody(body);
        body = null;
        physicsSystem.removeUpdateListener(this);

        bodyType = BodyType.StaticBody;
        isFastMoving = false;
        initialDamping = Physics.DAMPING_MEDIUM_STOP;
        syncPosition = false;
        headingLockedCount = 0;
    }

    @Override
    public void onPhysicsUpdate() {
        Vector2 gamePosition = Pools.vector2s.grabNew();
        gamePosition.set(body.getPosition()).scl(Physics.METERS_TO_PIXELS);
        positionComponent.setPosition(gamePosition);
        Pools.vector2s.freeCount(1);
    }

    private void assertNonStaticType() {
        if (RUN_SANITY_CHECKS && bodyType == BodyType.StaticBody) {
            throw new IllegalArgumentException("Can't move a static body. Did you forget to set body type?");
        }
    }
}
