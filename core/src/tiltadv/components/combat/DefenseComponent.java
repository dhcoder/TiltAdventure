package tiltadv.components.combat;

import dhcoder.libgdx.entity.AbstractComponent;

/**
 * Component that maintains logic for how much damage should be mitigated when this entity gets attacked.
 */
public final class DefenseComponent extends AbstractComponent {

    private int defense;

    public DefenseComponent() { reset(); }

    public int getDefense() {
        return defense;
    }

    public DefenseComponent setDefense(final int defense) {
        this.defense = defense;
        return this;
    }

    public int reduceDamage(final int damage) {
        int finalDamage = damage - defense;
        return Math.max(finalDamage, 1);
    }

    @Override
    public void reset() {
        defense = 0;
    }
}
