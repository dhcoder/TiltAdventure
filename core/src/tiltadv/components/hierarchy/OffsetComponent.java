package tiltadv.components.hierarchy;

import com.badlogic.gdx.math.Vector2;
import dhcoder.libgdx.entity.AbstractComponent;
import dhcoder.libgdx.entity.Entity;
import dhcoder.support.event.EventListener;
import dhcoder.support.time.Duration;
import tiltadv.components.body.TransformComponent;
import tiltadv.memory.Pools;

/**
 * A component that links a child with its parent (each which must have a {@link TransformComponent}) so that when one
 * moves, the other follows along.
 */
public final class OffsetComponent extends AbstractComponent {

    private final Vector2 offset = new Vector2();
    private final EventListener handleTranslateChanged = new EventListener() {
        @Override
        public void run(final Object sender) {
            if (isUpdating) {
                return;
            }

            if (sender == transformComponent) {
                syncParentToChild();
            }
            else if (sender == parentTransformComponent) {
                syncChildToParent();
            }
            else {
                throw new IllegalStateException("Unexpected translate event in OffsetComponent");
            }
        }
    };

    private boolean isUpdating;
    private TransformComponent transformComponent;
    private TransformComponent parentTransformComponent;

    public OffsetComponent setOffset(final Vector2 offset) {
        this.offset.set(offset);
        return this;
    }

    @Override
    public final void initialize(final Entity owner) {
        transformComponent = owner.requireComponent(TransformComponent.class);

        Entity parent = owner.requireComponent(ParentComponent.class).getParent();
        parentTransformComponent = parent.requireComponent(TransformComponent.class);

        transformComponent.onTranslateChanged.addListener(handleTranslateChanged);
        parentTransformComponent.onTranslateChanged.addListener(handleTranslateChanged);
    }

    @Override
    public final void reset() {
        offset.setZero();
        isUpdating = false;

        transformComponent.onTranslateChanged.removeListener(handleTranslateChanged);
        parentTransformComponent.onTranslateChanged.removeListener(handleTranslateChanged);

        transformComponent = null;
        parentTransformComponent = null;
    }

    @Override
    public void update(final Duration elapsedTime) {
        isUpdating = true;
        syncChildToParent();
        isUpdating = false;
    }

    private void syncChildToParent() {
        int mark = Pools.vector2s.mark();
        final Vector2 translate = Pools.vector2s.grabNew().set(parentTransformComponent.getTranslate());
        translate.add(offset);
        transformComponent.setTranslate(translate);
        Pools.vector2s.freeToMark(mark);
    }

    private void syncParentToChild() {
        int mark = Pools.vector2s.mark();
        final Vector2 translate = Pools.vector2s.grabNew().set(transformComponent.getTranslate());
        translate.sub(offset);
        parentTransformComponent.setTranslate(translate);
        Pools.vector2s.freeToMark(mark);
    }

}
