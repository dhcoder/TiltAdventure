package dhcoder.support.time;

import static dhcoder.support.utils.StringUtils.format;

/**
 * An class which represents a time duration.
 */
public class Duration {

    public static Duration zero() {
        return new Duration();
    }

    public static Duration fromSeconds(final float secs) {
        Duration duration = new Duration();
        duration.setSeconds(secs);
        return duration;
    }

    public static Duration fromMinutes(final float minutes) {
        Duration duration = new Duration();
        duration.setMinutes(minutes);
        return duration;
    }

    public static Duration fromMilliseconds(final float milliseconds) {
        Duration duration = new Duration();
        duration.setMilliseconds(milliseconds);
        return duration;
    }

    private float seconds;

    /**
     * Use {@link #fromSeconds(float)}, {@link #fromMinutes(float)}, or {@link #fromMilliseconds(float)} instead.
     */
    private Duration() {}

    public float getSeconds() {
        return seconds;
    }

    public void setSeconds(final float secs) {
        if (secs < 0f) {
            throw new IllegalArgumentException(format("Attempted to create a negative duration of {0} seconds", secs));
        }
        seconds = secs;
    }

    public float getMinutes() {
        return seconds / 60f;
    }

    public void setMinutes(final float minutes) {
        setSeconds(minutes * 60f);
    }

    public float getMilliseconds() {
        return seconds * 1000f;
    }

    public void setMilliseconds(final float milliseconds) {
        setSeconds(milliseconds / 1000f);
    }

    public void setFrom(final Duration duration) {
        setSeconds(duration.seconds);
    }

    public void addSeconds(final float secs) {
        setSeconds(getSeconds() + secs);
    }

    public void addMinutes(final float minutes) {
        setMinutes(getMinutes() + minutes);
    }

    public void addMilliseconds(final float milliseconds) {
        setMilliseconds(getMilliseconds() + milliseconds);
    }

    public void add(final Duration duration) {
        setSeconds(getSeconds() + duration.getSeconds());
    }

    public void setZero() { setSeconds(0f); }

    public boolean isZero() { return seconds == 0f; }
}
