package dhcoder.tool.javafx.control;

import dhcoder.tool.command.CommandManager;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.Pane;
import javafx.stage.PopupWindow;

import java.io.IOException;

/**
 * A dialog which can search through all registered, named actions.
 */
public final class CommandWindow extends PopupWindow {

    public CommandWindow(final CommandManager commandManager) {
        autoHideProperty().set(true);

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(CommandWindow.class.getResource("CommandWindowView.fxml"));
        try {
            Pane commandWindowView = loader.load();
            CommandWindowController controller = loader.getController();
            controller.setCommandManager(commandManager);
            getScene().setRoot(commandWindowView);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
