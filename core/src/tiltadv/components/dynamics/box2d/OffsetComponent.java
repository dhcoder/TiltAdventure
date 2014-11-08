package tiltadv.components.dynamics.box2d;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import dhcoder.libgdx.entity.AbstractComponent;
import dhcoder.libgdx.entity.Entity;
import dhcoder.libgdx.physics.PhysicsSystem;
import dhcoder.libgdx.physics.PhysicsUpdateListener;
import dhcoder.support.math.Angle;
import tiltadv.globals.Physics;
import tiltadv.globals.Services;
import tiltadv.memory.Pools;

import static dhcoder.support.contract.ContractUtils.requireNonNull;
import static dhcoder.support.contract.ContractUtils.requireTrue;

/**
 * A component that keeps two Box2D {@link BodyComponent}s in sync.
 */
public final class OffsetComponent extends AbstractComponent implements PhysicsUpdateListener {

    private Body remoteBody;
    private Body localBody;
    private Vector2 offset = new Vector2();
    private Angle angle = Angle.fromRadians(0f);

    public OffsetComponent setTargetBody(final Body body) {
        remoteBody = body;
        return this;
    }

    public OffsetComponent setOffset(final Vector2 offset) {
        Physics.toMeters(this.offset.set(offset));
        return this;
    }

    public OffsetComponent setAngle(final Angle angle) {
        this.angle.setFrom(angle);
        return this;
    }

    @Override
    public void initialize(final Entity owner) {
        requireNonNull(remoteBody, "Target body must be set");
        localBody = owner.requireComponentBefore(this, BodyComponent.class).getBody();
        requireTrue(localBody.getType() == BodyDef.BodyType.KinematicBody,
            "Offset components should only be used to drive kinematic bodies");

        Services.get(PhysicsSystem.class).addUpdateListener(this);
    }

    @Override
    public void reset() {
        Services.get(PhysicsSystem.class).removeUpdateListener(this);
        remoteBody = null;
        localBody = null;
        offset.setZero();
    }

    @Override
    public void onPhysicsUpdate() {
        Angle finalAngle = Pools.angles.grabNew();
        finalAngle.setRadians(remoteBody.getAngle()).add(angle);

        Vector2 transformedOffset = Pools.vector2s.grabNew().set(offset);
        transformedOffset.rotateRad(finalAngle.getRadians());
        transformedOffset.add(remoteBody.getPosition());

        localBody.setTransform(transformedOffset, finalAngle.getRadians());

        Pools.angles.freeCount(1);
        Pools.vector2s.freeCount(1);
    }
}
