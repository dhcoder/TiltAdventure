package tiltadv.components.hierarchy;

import dhcoder.libgdx.entity.AbstractComponent;
import dhcoder.libgdx.entity.Entity;

import static dhcoder.support.contract.ContractUtils.requireNonNull;

/**
 * Class that establishes a parent/child relationship between entities.
 */
public final class ParentComponent extends AbstractComponent {

    private Entity parent;

    public ParentComponent() { reset(); }

    public Entity getParent() {
        return parent;
    }

    public void setParent(final Entity parent) {
        this.parent = parent;
    }

    @Override
    public void initialize(final Entity owner) {
        requireNonNull(parent, "Parent must be set");
    }

    @Override
    public void reset() {
        parent = null;
    }
}
