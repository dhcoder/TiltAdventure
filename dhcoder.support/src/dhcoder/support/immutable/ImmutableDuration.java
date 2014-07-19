package dhcoder.support.immutable;

import dhcoder.support.time.Duration;

/**
 * A class that wraps a Duration, offering read-only access to it.
 */
public class ImmutableDuration extends Immutable<Duration> {

    public static ImmutableDuration fromSeconds(final float secs) {
        return new ImmutableDuration(Duration.fromSeconds(secs));
    }

    public static ImmutableDuration fromMinutes(final float minutes) {
        return new ImmutableDuration(Duration.fromMinutes(minutes));
    }

    public static ImmutableDuration fromMilliseconds(final float milliseconds) {
        return new ImmutableDuration(Duration.fromMilliseconds(milliseconds));
    }

    public ImmutableDuration(final Duration duration) {
        super(duration);
    }

    @Override
    public Duration toMutable() {
        return Duration.fromSeconds(wrappedMutable.getSeconds());
    }

    @Override
    public void copyInto(final Duration target) {
        target.setSeconds(wrappedMutable.getSeconds());
    }

    public float getSeconds() {
        return wrappedMutable.getSeconds();
    }

    public float getMinutes() {
        return wrappedMutable.getMinutes();
    }

    public float getMilliseconds() {
        return wrappedMutable.getMilliseconds();
    }
}
