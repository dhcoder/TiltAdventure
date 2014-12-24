package tiltadv.tools.scene.view;

import dhcoder.tool.command.Command;
import dhcoder.tool.javafx.fxutils.FxController;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;

/**
 * Window to show if no scene is open. It is often the first thing first time users to the tool will see, and as such,
 * it tries to convey useful first time information.
 */
public final class NoSceneController extends FxController {
    @FXML public Text textNewSceneCommand;
    @FXML private AnchorPane rootPane;
    @FXML private Text textCommandWindowShortcut;

    @Override
    public Parent getRoot() {
        return rootPane;
    }

    public void setCommandWindowCommand(final Command commandWindowCommand, final Command newSceneCommand) {
        textCommandWindowShortcut.setText(commandWindowCommand.getShortcutOpt().getValue().toString());
        textNewSceneCommand.setText(newSceneCommand.getName());

    }
}
