package tiltadv.immutable;

/**
 * Base class for wrapping a mutable object and offer an immutable interface to it. It is up to the children classes to
 * mimic only the read-only API of the inner class.
 */
public abstract class Immutable<T> {
    protected T wrappedMutable;

    protected Immutable(final T mutable) {
        this.wrappedMutable = mutable;
    }

    public abstract T toMutable();

    @Override
    public String toString() {
        return wrappedMutable.toString();
    }
}
