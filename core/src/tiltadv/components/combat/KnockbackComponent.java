package tiltadv.components.combat;

import com.badlogic.gdx.math.Vector2;
import dhcoder.libgdx.entity.AbstractComponent;
import dhcoder.libgdx.entity.Entity;
import dhcoder.support.time.Duration;
import tiltadv.components.body.MotionComponent;
import tiltadv.memory.Pools;

/**
 * Multiplier for how far this entity should get knocked back if hurt.
 */
public final class KnockbackComponent extends AbstractComponent {

    private static final float KNOCKBACK_MAGNITUTDE = 150f;
    public static final Duration DURATION = Duration.fromSeconds(.3f);

    private float multiplier;
    private MotionComponent motionComponent;

    public KnockbackComponent() { reset(); }

    public KnockbackComponent setMultiplier(final float multiplier) {
        this.multiplier = multiplier;
        return this;
    }

    @Override
    public void initialize(final Entity owner) {
        motionComponent = owner.requireComponent(MotionComponent.class);
    }

    @Override
    public void reset() {
        multiplier = 1;
        motionComponent = null;
    }

    public void knockback(final Vector2 direction) {
        knockback(direction, 1f);
    }

    public void knockback(final Vector2 direction, final float additionalMultiplier) {
        Vector2 impulse =
            Pools.vector2s.grabNew().set(direction).scl(KNOCKBACK_MAGNITUTDE * multiplier * additionalMultiplier);
        motionComponent.setImpulse(impulse, DURATION);
        Pools.vector2s.freeCount(1);
    }

}
