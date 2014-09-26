package tiltadv.components.hierarchy;

import dhcoder.libgdx.entity.AbstractComponent;
import dhcoder.libgdx.entity.Entity;

import java.util.ArrayList;

/**
 * Class that contains a list of children entities for this {@link Entity}.
 */
public abstract class ChildrenComponent extends AbstractComponent {

    private final ArrayList<Entity> children = new ArrayList<Entity>(4);
    private Entity owner;

    @Override
    public final void initialize(final Entity owner) {
        this.owner = owner;
        handleInitialize(owner);
    }

    @Override
    public final void reset() {
        handleReset();
        final int numChildren = children.size();
        for (int i = 0; i < numChildren; i++) {
            Entity child = children.get(i);
            child.free();
        }
        children.clear();
    }

    protected void add(final Entity child) {
        child.requireComponent(ParentComponent.class).setParent(owner);
        children.add(child);
    }

    protected void handleInitialize(final Entity owner) {}

    protected void handleReset() {}

    ;
}
