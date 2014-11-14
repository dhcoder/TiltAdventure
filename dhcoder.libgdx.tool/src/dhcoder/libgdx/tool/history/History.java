package dhcoder.libgdx.tool.history;

import java.util.Stack;

import static dhcoder.support.text.StringUtils.format;

/**
 * A class which manages the history of many {@link UndoValue} instances.
 */
public final class History {

    private static class UndoItem {
        public UndoValue undoValue;
        public Object oldValue;

        public UndoItem(final UndoValue undoValue, final Object oldValue) {
            this.undoValue = undoValue;
            this.oldValue = oldValue;
        }
    }

    private static class UndoGroup {
        public String description;
        public Stack<UndoItem> items = new Stack<UndoItem>();

        public UndoGroup(final String description) {
            this.description = description;
        }
    }

    public static int DEFAULT_HISTORY_DEPTH = 30;
    private final Stack<UndoGroup> undoStack = new Stack<UndoGroup>();
    private final Stack<UndoGroup> redoStack = new Stack<UndoGroup>();
    private final int maxHistoryDepth;
    private int isRecording;

    public History() {
        this(DEFAULT_HISTORY_DEPTH);
    }

    public History(final int maxHistoryDepth) {
        if (maxHistoryDepth < 1) {
            throw new IllegalArgumentException(format("Invalid history size, should be >= 1: {0}", maxHistoryDepth));
        }
        this.maxHistoryDepth = maxHistoryDepth;
    }

    public boolean canUndo() {
        return undoStack.size() > 0;
    }

    public boolean canRedo() {
        return redoStack.size() > 0;
    }

    public int getUndoDepth() {
        return undoStack.size();
    }

    public int getRedoDepth() {
        return redoStack.size();
    }

    public String getUndoDescription(final int index) {
        return undoStack.get(index).description;
    }

    public String getRedoDescription(final int index) {
        return redoStack.get(index).description;
    }

    public void undo() {
        if (!canUndo()) {
            throw new IllegalStateException("Nothing to undo");
        }

        if (isRecording > 0) {
            throw new IllegalStateException("Can't undo while mid-recording");
        }

        UndoGroup undoGroup = undoStack.pop();
        UndoGroup redoGroup = new UndoGroup(undoGroup.description);

        while (undoGroup.items.size() > 0) {
            UndoItem undoItem = undoGroup.items.pop();
            Object currValue = undoItem.undoValue.getValue();
            undoItem.undoValue.restoreValue(undoItem.oldValue);

            undoItem.oldValue = currValue;
            redoGroup.items.push(undoItem);
        }

        redoStack.push(redoGroup);
    }

    public void redo() {
        if (!canRedo()) {
            throw new IllegalStateException("Nothing to redo");
        }

        if (isRecording > 0) {
            throw new IllegalStateException("Can't redo while mid-recording");
        }

        UndoGroup redoGroup = redoStack.pop();
        UndoGroup undoGroup = new UndoGroup(redoGroup.description);

        while (redoGroup.items.size() > 0) {
            UndoItem redoItem = redoGroup.items.pop();
            Object currValue = redoItem.undoValue.getValue();
            redoItem.undoValue.restoreValue(redoItem.oldValue);

            redoItem.oldValue = currValue;
            undoGroup.items.push(redoItem);
        }

        undoStack.push(undoGroup);
    }

    public void startRecording(final String description) {
        if (isRecording == 0) {
            final UndoGroup undoGroup = new UndoGroup(description);
            undoStack.add(undoGroup);
        }

        isRecording++;
    }

    public void stopRecording() {
        if (isRecording == 0) {
            throw new IllegalStateException("History has mismatched startRecording/stopRecording pair");
        }

        --isRecording;

        if (isRecording == 0) {
            if (undoStack.peek().items.size() == 0) {
                undoStack.pop(); // Nothing happened during this recording
            }
            else {
                redoStack.clear();
                if (undoStack.size() > maxHistoryDepth) {
                    undoStack.removeElementAt(0); // Drop old history
                }
            }
        }
    }

    // Visible for testing
    int getActiveGroupSize() {
        return undoStack.peek().items.size();
    }

    void recordValue(final UndoValue undoValue, final Object value) {
        if (isRecording == 0) {
            return;
        }

        UndoGroup undoGroup = undoStack.peek();

        for (UndoItem undoItem : undoGroup.items) {
            if (undoItem.undoValue == undoValue) {
                return; // We're already recording an value for this instance
            }
        }

        undoGroup.items.push(new UndoItem(undoValue, value));
    }
}
