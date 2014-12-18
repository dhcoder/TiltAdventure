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

    private final CommandManager commandManager;

    public CommandWindow(final CommandManager commandManager) {
        this.commandManager = commandManager;

        autoHideProperty().set(true);

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(CommandWindow.class.getResource("CommandWindowView.fxml"));
        try {
            Pane commandWindowView = loader.load();
            CommandWindowController controller = loader.getController();
            controller.setCommandWindow(this);
            getScene().setRoot(commandWindowView);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public CommandManager getCommandManager() {
        return commandManager;
    }
}
