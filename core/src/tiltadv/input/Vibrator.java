package tiltadv.input;

import com.badlogic.gdx.Gdx;
import dhcoder.support.time.Duration;
import tiltadv.globals.Settings;

/**
 * Class which provides helper methods to handle vibrating the game.
 */
public final class Vibrator {

    /**
     * A duration to use with {@link #vibrate(Duration)} for a quick vibration burst (50 ms)
     */
    public static final Duration QUICK = Duration.fromMilliseconds(50);

    /**
     * A duration to use with {@link #vibrate(Duration)} for a moderate vibration (200 ms)
     */
    public static final Duration MEDIUM = Duration.fromMilliseconds(200);

    public void vibrate(final Duration duration) {
        if (Settings.VIBRATION_ENABLED) {
            Gdx.input.vibrate((int)duration.getMilliseconds());
        }
    }
}
