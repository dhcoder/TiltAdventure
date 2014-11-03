package tiltadv.components.combat;

import com.badlogic.gdx.math.Vector2;
import dhcoder.libgdx.entity.AbstractComponent;
import dhcoder.libgdx.entity.Entity;
import tiltadv.components.dynamics.box2d.BodyComponent;
import tiltadv.memory.Pools;

/**
 * Multiplier for how far this entity should get knocked back if hurt.
 */
public final class KnockbackComponent extends AbstractComponent {

    private static final float KNOCKBACK_MAGNITUTDE = 500f;

    private float multiplier;
    private BodyComponent bodyComponent;

    public KnockbackComponent() { reset(); }

    public KnockbackComponent setMultiplier(final float multiplier) {
        this.multiplier = multiplier;
        return this;
    }

    @Override
    public void initialize(final Entity owner) {
        bodyComponent = owner.requireComponent(BodyComponent.class);
    }

    @Override
    public void reset() {
        multiplier = 1;
        bodyComponent = null;
    }

    public void knockback(final Vector2 direction) {
        knockback(direction, 1f);
    }

    public void knockback(final Vector2 direction, final float additionalMultiplier) {
        Vector2 impulse =
            Pools.vector2s.grabNew().set(direction).scl(KNOCKBACK_MAGNITUTDE * multiplier * additionalMultiplier);
        bodyComponent.applyImpulse(impulse);
        Pools.vector2s.freeCount(1);
    }

}
