package tiltadv.components.hierarchy;

import dhcoder.libgdx.entity.AbstractComponent;
import dhcoder.libgdx.entity.Entity;
import tiltadv.components.body.PositionComponent;

/**
 * A component that links a child with its parent (each which must have a {@link PositionComponent}) so that when one
 * moves, the other follows along.
 *
 * This class works by piggybacking on an {@link OffsetComponent} which must be present on the same {@link Entity}
 */
public final class ChildOffsetComponent extends AbstractComponent {

    @Override
    public void initialize(final Entity owner) {
        OffsetComponent offsetComponent = owner.requireComponent(OffsetComponent.class);
        ParentComponent parentComponent = owner.requireComponent(ParentComponent.class);

        offsetComponent.setSourcePosition(parentComponent.getParent().requireComponent(PositionComponent.class));
    }
}
