package tiltadv.components.dynamics.box2d;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import dhcoder.libgdx.entity.AbstractComponent;
import dhcoder.libgdx.entity.Entity;
import dhcoder.libgdx.physics.PhysicsSystem;
import dhcoder.libgdx.physics.PhysicsUpdateListener;
import dhcoder.support.time.Duration;
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

    public OffsetComponent setTargetBody(final Body body) {
        remoteBody = body;
        return this;
    }

    public OffsetComponent setOffset(final Vector2 offset) {
        Physics.toMeters(this.offset.set(offset));
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
    public void update(final Duration elapsedTime) {
        super.update(elapsedTime);
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
        Vector2 transformedOffset = Pools.vector2s.grabNew().set(offset);
        remoteBody.getTransform().mul(transformedOffset);
        localBody.setTransform(transformedOffset, remoteBody.getAngle());
        Pools.vector2s.freeCount(1);
    }
}
