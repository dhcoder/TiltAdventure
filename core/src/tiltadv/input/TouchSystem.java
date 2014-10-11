package tiltadv.input;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import dhcoder.support.opt.Opt;
import tiltadv.components.input.TouchableComponent;
import tiltadv.memory.Pools;

/**
 * System that maintains a list of touchable components and provides an interface to select one by x,y position.
 */
public final class TouchSystem {
    private final Array<TouchableComponent> touchables;
    private final Opt<TouchableComponent> lastTouchableOpt = Opt.withNoValue();

    public TouchSystem(final int capacity) {
        touchables = new Array<TouchableComponent>(capacity);
    }

    public void add(final TouchableComponent touchableComponent) {
        touchables.add(touchableComponent);
    }

    public void remove(final TouchableComponent touchableComponent) {
        if (lastTouchableOpt.hasValue() && lastTouchableOpt.getValue() == touchableComponent) {
            lastTouchableOpt.clear();
        }

        touchables.removeValue(touchableComponent, true);
    }

    public void handleTouch(final float localX, final float localY) {
        if (lastTouchableOpt.hasValue()) {
            lastTouchableOpt.getValue().deselect();
            lastTouchableOpt.clear();
        }

        int numTouchables = touchables.size;
        final Vector2 touchPosition = Pools.vector2s.grabNew().set(localX, localY);
        for (int i = 0; i < numTouchables; i++) {
            TouchableComponent touchableComponent = touchables.get(i);
            if (touchableComponent.select(touchPosition)) {
                lastTouchableOpt.set(touchableComponent);
                break;
            }
        }
        Pools.vector2s.freeCount(1);
    }
}