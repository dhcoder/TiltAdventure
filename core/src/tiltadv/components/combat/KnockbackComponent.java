package tiltadv.components.combat;

import dhcoder.libgdx.entity.AbstractComponent;

/**
 * Multiplier for how far this entity should get knocked back if hurt.
 */
public final class KnockbackComponent extends AbstractComponent {

    private float multiplier;

    public KnockbackComponent() { reset(); }

    public float getMultiplier() {
        return multiplier;
    }

    public KnockbackComponent setMultiplier(final float multiplier) {
        this.multiplier = multiplier;
        return this;
    }

    @Override
    public void reset() {
        multiplier = 1;
    }
}
