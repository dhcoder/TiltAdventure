package tiltadv.components.combat;

import dhcoder.libgdx.entity.AbstractComponent;

/**
 * Component that maintains logic for how much damage this entity passes onto a target entity.
 */
public final class AttackComponent extends AbstractComponent {

    private int strength;

    public AttackComponent() { reset(); }

    public int getStrength() {
        return strength;
    }

    public AttackComponent setStrength(final int strength) {
        this.strength = strength;
        return this;
    }

    @Override
    public void reset() {
        strength = 1;
    }
}
