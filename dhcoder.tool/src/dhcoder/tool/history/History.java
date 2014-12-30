package dhcoder.tool.history;

import java.util.ArrayList;
import java.util.List;
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
        public List<UndoItem> items = new ArrayList<UndoItem>();

        public UndoGroup(final String description) {
            this.description = description;
        }
    }

    public static int DEFAULT_HISTORY_DEPTH = 30;
    private final Stack<UndoGroup> undoStack = new Stack<UndoGroup>();
    private final Stack<UndoGroup> redoStack = new Stack<UndoGroup>();
    private final int maxHistoryDepth;
    private int isRecording;
    /**
     * currentPtr points to the position in the history that represents the "current" state. This is useful for an
     * owning system to know if, say, the state of data that it is managing has changed.
     *
     * See also: {@link #isCurrent()} and {@link #markCurrent()}
     */
    private int currentPtr;

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
        restoreAndReverse(undoGroup);
        redoStack.push(undoGroup);
    }

    public void redo() {
        if (!canRedo()) {
            throw new IllegalStateException("Nothing to redo");
        }

        if (isRecording > 0) {
            throw new IllegalStateException("Can't redo while mid-recording");
        }

        UndoGroup redoGroup = redoStack.pop();
        restoreAndReverse(redoGroup);
        undoStack.push(redoGroup);
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
                if (undoStack.size() <= currentPtr) {
                    // If here, it means we did a bunch of actions, marked a state as current, then undid those changes,
                    // and then started recording some new changes. Our old, current state is now lost until
                    // markCurrent is called again.
                    currentPtr = -1;
                }

                redoStack.clear();
                if (undoStack.size() > maxHistoryDepth) {
                    undoStack.removeElementAt(0); // Drop old history
                }
            }
        }
    }

    public void markCurrent() {
        currentPtr = undoStack.size();
    }

    public boolean isCurrent() {
        return currentPtr == undoStack.size();
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

        undoGroup.items.add(new UndoItem(undoValue, value));
    }

    /**
     * This operation is useful in order to move a group from the undo stack to the redo stack or vice versa (the
     * order of operations in one should be proceed in the opposite order in the other)
     */
    private void restoreAndReverse(final UndoGroup undoGroup) {
        for (UndoItem undoItem : undoGroup.items) {
            Object currValue = undoItem.undoValue.getValue();
            undoItem.undoValue.restoreValue(undoItem.oldValue);
            undoItem.oldValue = currValue;
        }

        // Ex. 1 2 3 4 -> *4 2 3 *1 -> 4 *3 *2 1
        //     1 2 3 4 5 -> *5 2 3 4 *1 -> 5 *4 3 *2 1
        for (int i = 0; i <= (undoGroup.items.size() / 2); i++) {
            int mirrorIndex = undoGroup.items.size() - i - 1;
            UndoItem temp = undoGroup.items.get(i);
            undoGroup.items.set(i, undoGroup.items.get(mirrorIndex));
            undoGroup.items.set(mirrorIndex, temp);
        }
    }
}
