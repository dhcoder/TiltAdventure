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

    /**
     * Set commands we want to show advice about for new users.
     */
    public void setTooltipCommands(final Action actionWindowCommand, final Action newSceneCommand) {
        textCommandWindowShortcut.setText(actionWindowCommand.getAccelerator().getName());
        textNewSceneCommand.setText(newSceneCommand.getText());
    }
}
