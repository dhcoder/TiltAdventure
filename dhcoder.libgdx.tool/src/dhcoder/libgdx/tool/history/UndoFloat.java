package dhcoder.libgdx.tool.history;

/**
 * An undo/redo float value.
 */
public final class UndoFloat extends UndoValue<Float> {

    public UndoFloat(final History history, final Float initialValue) {
        super(history, initialValue);
    }

    @Override
    protected boolean valueEquals(final Float value1, final Float value2) {
        return value1.floatValue() == value2.floatValue();
    }
}
