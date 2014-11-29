package dhcoder.libgdx.tool.scene2d;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Timer;

import static com.badlogic.gdx.utils.Timer.Task;

/**
 * Class which can be used with keydown and keyup events to enable keyboard repeating in Scene2d UI.
 */
public class KeyboardRepeatTask extends Task {

    private final Actor actor;
    private int keycode = -1;
    public static final float KEY_REPEAT_INITIAL_TIME = 0.4f;
    public static final float KEY_REPEAT_TIME = 0.1f;

    public KeyboardRepeatTask(final Actor actor) {
        this.actor = actor;
    }

    public void scheduleRepeat(final int keycode) {
        if (this.keycode == keycode) {
            return;
        }
        unschedule();
        this.keycode = keycode;
        Timer.schedule(this, KEY_REPEAT_INITIAL_TIME, KEY_REPEAT_TIME);
    }

    public void unschedule() {
        keycode = -1;
        cancel();
    }

    @Override
    public void run() {
        Stage stage = actor.getStage();
        if (stage == null) {
            return;
        }

        stage.keyDown(keycode);
    }
}
