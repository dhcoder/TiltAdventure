package tiltadv.tools.scene.view;

import dhcoder.tool.javafx.utils.FxController;
import javafx.fxml.FXML;
import javafx.scene.text.Text;
import org.controlsfx.control.action.Action;

/**
 * Window to show if no scene is open. It is often the first thing first time users to the tool will see, and as such,
 * it tries to convey useful first time information.
 */
public final class NoSceneController extends FxController {
    @FXML public Text textNewSceneCommand;
    @FXML private Text textCommandWindowShortcut;

    public void setCommandWindowCommand(final Action commandWindowCommand, final Action newSceneCommand) {
        textCommandWindowShortcut.setText(commandWindowCommand.getAccelerator().getDisplayText());
        textNewSceneCommand.setText(newSceneCommand.getText());
    }
}
