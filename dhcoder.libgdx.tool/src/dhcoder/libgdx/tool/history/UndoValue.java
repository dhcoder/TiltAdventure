package dhcoder.libgdx.tool.history;

/**
 * Base class for a value that supports undo and redo operations when associated with a {@link History} instance.
 */
public abstract class UndoValue<T> {
    private History history;
    private T value;

    public UndoValue(final History history, final T initialValue) {
        this.history = history;
        value = initialValue;
    }

    public final T getValue() {
        return value;
    }

    public final void setValue(final T value) {
        if (!valueEquals(this.value, value)) {
            history.recordValue(this, this.value);
            this.value = value;
        }
    }

    final void restoreValue(final T value) {
        this.value = value;
    }

    protected abstract boolean valueEquals(final T value1, final T value2);
}
