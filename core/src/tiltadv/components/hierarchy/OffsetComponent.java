package tiltadv.components.hierarchy;

import com.badlogic.gdx.math.Vector2;
import dhcoder.libgdx.entity.AbstractComponent;
import dhcoder.libgdx.entity.Entity;
import dhcoder.support.event.EventListener;
import dhcoder.support.opt.Opt;
import tiltadv.components.body.PositionComponent;
import tiltadv.memory.Pools;

import static dhcoder.support.contract.ContractUtils.requireFalse;

/**
 * A component that links a child with its parent (each which must have a {@link PositionComponent}) so that when one
 * moves, the other follows along.
 */
public final class OffsetComponent extends AbstractComponent {

    private final Vector2 offset = new Vector2();
    private final EventListener handleTranslateChanged = new EventListener() {
        @Override
        public void run(final Object sender) {
            if (isSyncing || !sourcePositionComponentOpt.hasValue()) {
                return;
            }

            if (sender == positionComponent) {
                reverseSyncToSource();
            }
            else if (sender == sourcePositionComponentOpt.getValue()) {
                syncToSource();
            }
        }
    };

    private boolean isSyncing;
    private PositionComponent positionComponent;
    private Opt<PositionComponent> sourcePositionComponentOpt = Opt.withNoValue();

    public OffsetComponent setSourcePosition(final PositionComponent sourcePositionComponent) {
        requireFalse(sourcePositionComponentOpt.hasValue(),
            "Offset's source position should only be set once (unless cleared).");
        this.sourcePositionComponentOpt.set(sourcePositionComponent);
        sourcePositionComponent.onChanged.addListener(handleTranslateChanged);
        return this;
    }

    public OffsetComponent clearSourcePosition() {
        if (!sourcePositionComponentOpt.hasValue())
            return this;

        sourcePositionComponentOpt.getValue().onChanged.removeListener(handleTranslateChanged);
        sourcePositionComponentOpt.clear();
        return this;
    }

    public OffsetComponent setOffset(final Vector2 offset) {
        this.offset.set(offset);
        syncToSource();
        return this;
    }

    @Override
    public void initialize(final Entity owner) {
        positionComponent = owner.requireComponent(PositionComponent.class);
        positionComponent.onChanged.addListener(handleTranslateChanged);
    }

    @Override
    public void reset() {
        offset.setZero();
        isSyncing = false;

        clearSourcePosition();
        positionComponent.onChanged.removeListener(handleTranslateChanged);

        positionComponent = null;
        sourcePositionComponentOpt = null;
    }

    private void syncToSource() {
        isSyncing = true;
        int mark = Pools.vector2s.mark();
        final Vector2 translate = Pools.vector2s.grabNew().set(sourcePositionComponentOpt.getValue().getPosition());
        translate.add(offset);
        positionComponent.setPosition(translate, false);
        Pools.vector2s.freeToMark(mark);
        isSyncing = false;
    }

    private void reverseSyncToSource() {
        isSyncing = true;
        int mark = Pools.vector2s.mark();
        final Vector2 translate = Pools.vector2s.grabNew().set(positionComponent.getPosition());
        translate.sub(offset);
        sourcePositionComponentOpt.getValue().setPosition(translate, false);
        Pools.vector2s.freeToMark(mark);
        isSyncing = false;
    }

}
