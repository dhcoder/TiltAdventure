package tiltadv.components.box2d;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import dhcoder.libgdx.entity.AbstractComponent;
import dhcoder.libgdx.entity.Entity;
import dhcoder.libgdx.physics.PhysicsElement;
import dhcoder.libgdx.physics.PhysicsSystem;
import dhcoder.support.math.Angle;
import dhcoder.support.time.Duration;
import tiltadv.components.body.PositionComponent;
import tiltadv.globals.Physics;
import tiltadv.globals.Services;
import tiltadv.memory.Pools;

import static dhcoder.support.contract.ContractUtils.requireNull;

/**
 * A component that encapsulates an entity's physical body - represented by a Box2D {@link Body}. This maintains the
 * knowledge of an entity's position, motion, and heading.
 *
 * If a body is present on an entity, it expects the presence of a {@link PositionComponent} and will take over the role
 * of setting it.
 */
public final class BodyComponent extends AbstractComponent implements PhysicsElement {

    /**
     * Box2D objects take too long to come to rest, so just manually stop them ourselves past a certain epsilon
     */
    private static final float STOP_EPSILON = 10f;
    private final Vector2 targetPosition = new Vector2(); // Position in meters
    private final Vector2 gameVelocity = new Vector2(); // Velocity in units of pixels per second
    private final Angle heading = Angle.fromRadians(0f);
    private BodyType bodyType = BodyType.StaticBody;
    private Body body;
    private boolean isFastMoving;
    private float damping = 10f;
    private boolean syncPosition;
    private int headingLockedCount;
    private PositionComponent positionComponent;

    public BodyComponent setBodyType(final BodyType bodyType) {
        requireNull(body, "Can't set body type after entity is initialized");

        this.bodyType = bodyType;
        return this;
    }

    /**
     * Set a linear damping value, if you want the object to gradually come to a stop over time. The actual value used
     * here probably requires tweaking to get the right effect, but setting it to 10f means the character will come to
     * a stop after about a second.
     */
    public BodyComponent setDamping(final float damping) {
        requireNull(body, "Can't set damping value after entity is initialized");

        this.damping = damping;
        return this;
    }

    public BodyComponent setFastMoving(final boolean isFastMoving) {
        requireNull(body, "Can't set body to fast moving after entity is initialized");

        this.isFastMoving = isFastMoving;
        return this;
    }

    /**
     * Set the position we want to try to get to by the next frame. That position may be impossible because physics will
     * prevent this entity from getting there, at which point, this object should move as best as it can based on the
     * scene's constraints.
     */
    public BodyComponent setDesiredPosition(final Vector2 position) {
        targetPosition.set(position).scl(Physics.PIXELS_TO_METERS);
        syncPosition = true;
        return this;
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
    public void update(final Duration elapsedTime) {

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

        if (getVelocity().isZero(STOP_EPSILON)) {
            Vector2 velocity = Pools.vector2s.grabNew(); // (0,0) velocity is exactly what we want
            setVelocity(velocity);
            Pools.vector2s.freeCount(1);
        }
    }

    @Override
    public void initialize(final Entity owner) {

        positionComponent = owner.requireComponent(PositionComponent.class);

        final PhysicsSystem physicsSystem = Services.get(PhysicsSystem.class);

        {
            BodyDef bodyDef = Pools.bodyDefs.grabNew();
            bodyDef.type = bodyType;
            bodyDef.bullet = isFastMoving;
            bodyDef.position.set(positionComponent.getPosition()).scl(Physics.PIXELS_TO_METERS);
            bodyDef.linearDamping = damping;
            body = physicsSystem.getWorld().createBody(bodyDef);
            body.setUserData(this);
            Pools.bodyDefs.freeCount(1);
        }

        physicsSystem.addElement(this);
    }

    @Override
    public void reset() {
        positionComponent = null;

        targetPosition.setZero();
        gameVelocity.setZero();
        heading.setRadians(0f);

        final PhysicsSystem physicsSystem = Services.get(PhysicsSystem.class);
        physicsSystem.getWorld().destroyBody(body);
        body = null;
        physicsSystem.removeElement(this);

        bodyType = BodyType.StaticBody;
        isFastMoving = false;
        damping = 10f;
        syncPosition = false;
        headingLockedCount = 0;
    }

    @Override
    public void syncWithPhysics() {
        Vector2 gamePosition = Pools.vector2s.grabNew();
        gamePosition.set(body.getPosition()).scl(Physics.METERS_TO_PIXELS);
        positionComponent.setPosition(gamePosition);
        Pools.vector2s.freeCount(1);
    }
}
