package dhcoder.libgdx.tool.history;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

public class HistoryTest {

    @Test
    public void testUndoSingleValue() {
        History history = new History();
        UndoInt undoInt = new UndoInt(history, 1);
        assertThat(undoInt.getValue(), equalTo(1));

        history.startRecording("Set int 1 -> 2");
        undoInt.setValue(2);
        history.stopRecording();
        assertThat(undoInt.getValue(), equalTo(2));

        history.startRecording("Set int 2 -> 3");
        undoInt.setValue(3);
        history.stopRecording();
        assertThat(undoInt.getValue(), equalTo(3));

        history.undo();
        assertThat(undoInt.getValue(), equalTo(2));

        history.undo();
        assertThat(undoInt.getValue(), equalTo(1));

        assertThat(history.canUndo(), equalTo(false));
    }

    @Test
    public void testRedoSingleValue() {
        History history = new History();
        UndoInt undoInt = new UndoInt(history, 1);
        history.startRecording("Set int 1 -> 2");
        undoInt.setValue(2);
        history.stopRecording();
        history.startRecording("Set int 2 -> 3");
        undoInt.setValue(3);
        history.stopRecording();
        assertThat(undoInt.getValue(), equalTo(3));

        history.undo();
        history.undo();
        assertThat(undoInt.getValue(), equalTo(1));

        assertThat(history.getRedoDepth(), equalTo(2));

        history.redo();
        assertThat(undoInt.getValue(), equalTo(2));

        history.redo();
        assertThat(undoInt.getValue(), equalTo(3));
    }

    @Test
    public void testUndoPairOfValues() {
        History history = new History();
        UndoInt undoInt = new UndoInt(history, 1);
        UndoFloat undoFloat = new UndoFloat(history, 2.0f);
        assertThat(undoInt.getValue(), equalTo(1));
        assertThat(undoFloat.getValue(), equalTo(2.0f));

        history.startRecording("Multiply by 2");
        undoInt.setValue(undoInt.getValue() * 2);
        undoFloat.setValue(undoFloat.getValue() * 2);
        history.stopRecording();
        assertThat(undoInt.getValue(), equalTo(2));
        assertThat(undoFloat.getValue(), equalTo(4.0f));

        history.undo();
        assertThat(undoInt.getValue(), equalTo(1));
        assertThat(undoFloat.getValue(), equalTo(2.0f));
    }

    @Test
    public void extraneousUndoValuesAreIgnored() {
        History history = new History();
        UndoInt undoInt = new UndoInt(history, 1);

        history.startRecording("Lots of calls to set value");
        for (int i = 2; i <= 100; i++) {
            undoInt.setValue(i);
        }
        history.stopRecording();
        assertThat(undoInt.getValue(), equalTo(100));

        assertThat(history.getActiveGroupSize(), equalTo(1));

        history.undo();

        assertThat(undoInt.getValue(), equalTo(1));
    }

    @Test
    public void noUndoRecordingIsDoneIfNoValuesChange() {
        History history = new History();
        UndoInt undoInt = new UndoInt(history, 1);

        history.startRecording("This will be discarded");
        for (int i = 0; i < 100; i++) {
            undoInt.setValue(1);
        }
        history.stopRecording();
        assertThat(history.canUndo(), equalTo(false)); // No undo recording to go back to
    }
}