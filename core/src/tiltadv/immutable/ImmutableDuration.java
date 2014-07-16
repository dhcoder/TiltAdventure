package tiltadv.immutable;

import dhcoder.support.time.Duration;

/**
 * A class that wraps a Duration, offering read-only access to it.
 */
public class ImmutableDuration extends Immutable<Duration> {

    public ImmutableDuration(final Duration duration) {
        super(duration);
    }

    @Override
    public Duration toMutable() {
        return Duration.fromSeconds(wrappedMutable.getSeconds());
    }

    public float inSeconds() {
        return wrappedMutable.getSeconds();
    }

    public float inMinutes() {
        return wrappedMutable.getMinutes();
    }

    public float inMilliseconds() {
        return wrappedMutable.getMilliseconds();
    }
}
