package tiltadv.tools.scene.view;

import dhcoder.tool.command.Command;
import javafx.fxml.FXML;
import javafx.scene.text.Text;

/**
 * Window to show if no scene is open. It is often the first thing first time users to the tool will see, and as such,
 * it tries to convey useful first time information.
 */
public final class NoSceneViewController {
    @FXML private Text textCommandWindowShortcut;

    public void setCommandWindowCommand(final Command commandWindowCommand) {
        textCommandWindowShortcut.setText(commandWindowCommand.getShortcutOpt().getValue().toString());
    }
}
