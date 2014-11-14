package dhcoder.libgdx.tool.history;

/**
 * An undo/redo integer value.
 */
public final class UndoInt extends UndoValue<Integer> {

    public UndoInt(final History history, final Integer initialValue) {
        super(history, initialValue);
    }

    @Override
    protected boolean valueEquals(final Integer value1, final Integer value2) {
        return value1.intValue() == value2.intValue();
    }
}
