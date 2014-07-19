package dhcoder.support.immutable;

import dhcoder.support.opt.Opt;

/**
 * A class that wraps an Opt, offering read-only access to it.
 */
public final class ImmutableOpt<T> extends Immutable<Opt<T>> {

    public static <T> ImmutableOpt<T> of(final T value) throws IllegalArgumentException {
        return new ImmutableOpt<T>(Opt.of(value));
    }

    public static <T> ImmutableOpt<T> ofNullable(final T value) {
        return new ImmutableOpt<T>(Opt.ofNullable(value));
    }

    public static <T> ImmutableOpt<T> withNoValue() {
        return new ImmutableOpt(Opt.withNoValue());
    }


    public ImmutableOpt(final Opt<T> opt) {
        super(opt);
    }

    public Opt<T> toMutable() {
        Opt<T> opt = Opt.withNoValue();
        opt.setFrom(wrappedMutable);
        return opt;
    }

    @Override
    public void copyInto(final Opt<T> target) {
        target.setFrom(wrappedMutable);
    }

    public T getValue() {
        return wrappedMutable.getValue();
    }

    public boolean hasValue() {
        return wrappedMutable.hasValue();
    }
}
