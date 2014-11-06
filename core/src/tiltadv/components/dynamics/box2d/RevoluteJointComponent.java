package tiltadv.components.dynamics.box2d;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Joint;
import com.badlogic.gdx.physics.box2d.JointDef;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;
import dhcoder.libgdx.entity.Entity;
import dhcoder.support.math.Angle;
import tiltadv.memory.Pools;

import static dhcoder.support.contract.ContractUtils.requireNonNull;
import static dhcoder.support.contract.ContractUtils.requireNull;

/**
 * A component that encapsulates a Box2D {@link Joint} which should be attached to two Box2D {@link Body} objects - one
 * on this {@link Entity} and one on a target {@link Entity}.
 */
public final class RevoluteJointComponent extends JointComponent<RevoluteJointComponent> {

    private final Vector2 localAnchorA = new Vector2();
    private final Vector2 localAnchorB = new Vector2();

    public RevoluteJointComponent setAnchorA(final Vector2 anchor) {
        localAnchorA.set(anchor);
        return this;
    }

    public RevoluteJointComponent setAnchorB(final Vector2 anchor) {
        localAnchorB.set(anchor);
        return this;
    }

    @Override
    protected JointDef createJointDef(final Body bodyA, final Body bodyB) {
        localAnchorB.set(-.5f, 0);
        RevoluteJointDef revoluteJointDef = Pools.revoluteJointDefs.grabNew();
        revoluteJointDef.bodyA = bodyA;
        revoluteJointDef.bodyB = bodyB;
        revoluteJointDef.localAnchorA.set(localAnchorA);
        revoluteJointDef.localAnchorB.set(localAnchorB);
        revoluteJointDef.enableLimit = true;
        revoluteJointDef.lowerAngle = -Angle.HALF_PI;
        revoluteJointDef.upperAngle = Angle.HALF_PI;
        revoluteJointDef.motorSpeed = Angle.PI * 100f;
        revoluteJointDef.enableMotor = true;

        return revoluteJointDef;
    }

    @Override
    protected void free(final JointDef jointDef) {
        requireNonNull(jointDef.bodyA, "The joint definition we are freeing is valid");
        Pools.revoluteJointDefs.freeCount(1);
        requireNull(jointDef.bodyA, "We freed the expected joint from the pool");
    }

    @Override
    protected void handleReset() {
        localAnchorA.setZero();
        localAnchorB.setZero();
    }
}
