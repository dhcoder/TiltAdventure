package dhcoder.libgdx.tool.scene2d;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;

/**
 * A special {@link InputListener} which will listen to keypresses sent to this actor and schedule them to repeat if
 * the key is held down. This should be the first listener registered with this actor to make sure other listeners don't
 * swallow any keypresses first!
 */
public final class KeyboardRepeater extends InputListener {
    KeyboardRepeatTask keyboardRepeatTask;

    public KeyboardRepeater(final Actor actor) {
        this.keyboardRepeatTask = new KeyboardRepeatTask(actor);
    }

    /**
     * Manually stop this keyboard repeater from firing, which is useful if you got a keydown without a matched keyup,
     * such as if you close a window while a key is being held down.
     */
    public void cancel() {
        keyboardRepeatTask.unschedule();
    }

    @Override
    public boolean keyDown(final InputEvent event, final int keycode) {
        keyboardRepeatTask.scheduleRepeat(keycode);
        return false;
    }

    @Override
    public boolean keyUp(final InputEvent event, final int keycode) {
        keyboardRepeatTask.unschedule();
        return false;
    }
}
