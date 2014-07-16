package tiltadv.immutable;

import dhcoder.support.opt.Opt;

/**
 * A class that wraps an Opt, offering read-only access to it.
 */
public final class ImmutableOpt<T> extends Immutable<Opt<T>> {

    public ImmutableOpt(final Opt<T> opt) {
        super(opt);
    }

    public Opt<T> toMutable() {
        Opt<T> opt = Opt.withNoValue();
        opt.setFrom(wrappedMutable);
        return opt;
    }

    public T getValue() {
        return wrappedMutable.getValue();
    }

    public boolean hasValue() {
        return wrappedMutable.hasValue();
    }
}
