package dhcoder.support.time;

/**
 * An immutable class which represents a time duration.
 */
public class Duration {
    private final float seconds;

    private Duration(final float seconds) {
        this.seconds = seconds;
    }

    public static Duration fromSeconds(final float secs) {
        return new Duration(secs);
    }

    public static Duration fromMinutes(final float minutes) {
        return new Duration(minutes * 60f);
    }

    public static Duration fromMilliseconds(final float milliseconds) {
        return new Duration(milliseconds / 1000f);
    }

    public float inSeconds() {
        return seconds;
    }

    public float inMinutes() {
        return seconds / 60f;
    }

    public float inMilliseconds() {
        return seconds * 1000f;
    }
}
