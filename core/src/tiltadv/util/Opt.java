package tiltadv.util;

/**
 * A class which represents an nullable value - that is, like a reference, it either has a value or not. Unlike a
 * reference, if an Opt doesn't have a value and you try to access it, it will throw an {@link IllegalStateException}.
 * <p/>
 * The practice encouraged by this class is to never use nulls directly - instead, assume a reference always indicates
 * a non-null value. If you have a method that needs to sometimes return null, or perhaps take in argument where it
 * makes sense for it to sometime be null, use this class instead. It marks the use-case explicitly and forces good
 * habits in the programmer by having them explicitly check for the existence of a value before using it.
 */
public final class Opt<T> {

    private T value;

    /**
     * Create an optional without a value.
     */
    public Opt() {
        // Intentionally doing nothing leaves this.value null.
    }

    /**
     * Create an optional with an initial value. This value shouldn't be null - use the default constructor instead if
     * you need a valueless optional.
     *
     * @throws IllegalArgumentException if the passed in value is null.
     */
    public Opt(T value) throws IllegalArgumentException {
        setValue(value);
    }

    /**
     * Create an optional from another optional.
     */
    public Opt(Opt<T> rhs) {
        this.value = rhs.value;
    }

    /**
     * Clears the value of this optional.
     */
    public void clearValue() {
        value = null;
    }

    /**
     * Returns the current value of this optional, or throws an exception otherwise. You may consider checking {@link
     * #hasValue()} first before calling this method.
     *
     * @throws IllegalStateException if this optional doesn't currently have a value.
     */
    public T getValue() {
        if (value == null) {
            throw new IllegalStateException("getValue called on an optional without a value.");
        }
        return value;
    }

    /**
     * Sets this optional to a new value. This value shouldn't be null - use {@link #clearValue()} instead if you need
     * to clear the current value.
     *
     * @throws IllegalArgumentException if the passed in value is null.
     */
    public void setValue(T value) {
        if (value == null) {
            throw new IllegalArgumentException("Can't set an optional to null! Use clearValue instead.");
        }
        this.value = value;
    }

    /**
     * Returns true if this optional currently has a value set.
     */
    public boolean hasValue() {
        return value != null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }

        Opt rhs = (Opt)o;

        if (value != null ? !value.equals(rhs.value) : rhs.value != null) { return false; }

        return true;
    }

    @Override
    public int hashCode() {
        return value != null ? value.hashCode() : 0;
    }
}
