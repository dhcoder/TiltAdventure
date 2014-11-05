package tiltadv.components.dynamics.box2d;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Joint;
import com.badlogic.gdx.physics.box2d.JointDef;
import dhcoder.libgdx.entity.AbstractComponent;
import dhcoder.libgdx.entity.Entity;

import static dhcoder.support.contract.ContractUtils.requireNonNull;

/**
 * A component that encapsulates a Box2D {@link Joint} which should be attached to two Box2D {@link Body} objects - one
 * on this {@link Entity} and one on a target {@link Entity}.
 */
public abstract class JointComponent<T extends JointComponent> extends AbstractComponent {

    private Body remoteBody;
    private Body localBody;
    private Joint joint;

    public final Joint getJoint() {
        return joint;
    }

    public final T setTargetBody(final Body body) {
        remoteBody = body;
        return (T)this;
    }

    @Override
    public final void initialize(final Entity owner) {
        requireNonNull(remoteBody, "Target body must be set");
        localBody = owner.requireComponentBefore(this, BodyComponent.class).getBody();
        JointDef jointDef = createJointDef(remoteBody, localBody);
        joint = localBody.getWorld().createJoint(jointDef);
        free(jointDef);
    }

    @Override
    public final void reset() {
        handleReset();
        remoteBody = null;
        localBody = null;
        joint = null;
    }

    protected abstract JointDef createJointDef(final Body bodyA, final Body bodyB);
    protected abstract void free(JointDef jointDef);
    protected void handleReset() {}
}
