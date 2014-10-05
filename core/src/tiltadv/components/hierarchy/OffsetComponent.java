package tiltadv.components.hierarchy;

import com.badlogic.gdx.math.Vector2;
import dhcoder.libgdx.entity.AbstractComponent;
import dhcoder.libgdx.entity.Entity;
import dhcoder.support.event.EventListener;
import dhcoder.support.time.Duration;
import tiltadv.components.body.PositionComponent;
import tiltadv.memory.Pools;

/**
 * A component that links a child with its parent (each which must have a {@link PositionComponent}) so that when one
 * moves, the other follows along.
 */
public final class OffsetComponent extends AbstractComponent {

    private final Vector2 offset = new Vector2();
    private final EventListener handleTranslateChanged = new EventListener() {
        @Override
        public void run(final Object sender) {
            if (sender == positionComponent) {
                syncParentToChild();
            }
            else if (sender == parentPositionComponent) {
                syncChildToParent();
            }
        }
    };

    private boolean isSyncing;
    private PositionComponent positionComponent;
    private PositionComponent parentPositionComponent;

    public OffsetComponent setOffset(final Vector2 offset) {
        this.offset.set(offset);
        syncChildToParent();
        return this;
    }

    @Override
    public void initialize(final Entity owner) {
        positionComponent = owner.requireComponent(PositionComponent.class);

        Entity parent = owner.requireComponent(ParentComponent.class).getParent();
        parentPositionComponent = parent.requireComponent(PositionComponent.class);

        positionComponent.onChanged.addListener(handleTranslateChanged);
        parentPositionComponent.onChanged.addListener(handleTranslateChanged);
    }

    @Override
    public void reset() {
        offset.setZero();
        isSyncing = false;

        positionComponent.onChanged.removeListener(handleTranslateChanged);
        parentPositionComponent.onChanged.removeListener(handleTranslateChanged);

        positionComponent = null;
        parentPositionComponent = null;
    }

    @Override
    public void update(final Duration elapsedTime) {
        syncChildToParent();
    }

    private void syncChildToParent() {
        isSyncing = true;
        int mark = Pools.vector2s.mark();
        final Vector2 translate = Pools.vector2s.grabNew().set(parentPositionComponent.getPosition());
        translate.add(offset);
        positionComponent.setPosition(translate, false);
        Pools.vector2s.freeToMark(mark);
        isSyncing = false;
    }

    private void syncParentToChild() {
        isSyncing = true;
        int mark = Pools.vector2s.mark();
        final Vector2 translate = Pools.vector2s.grabNew().set(positionComponent.getPosition());
        translate.sub(offset);
        parentPositionComponent.setPosition(translate, false);
        Pools.vector2s.freeToMark(mark);
        isSyncing = false;
    }

}
