package dhcoder.tool.history;

/**
 * A general undo/redo class, useful for any object type where equality means it's the same instance of the object.
 */
public final class UndoPtr<T> extends UndoValue<T> {
    public UndoPtr(final History history, final T initialValue) {
        super(history, initialValue);
    }

    @Override
    protected boolean valueEquals(final T value1, final T value2) {
        return value1 == value2;
    }
}
