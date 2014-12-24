package dhcoder.tool.javafx.control;

import dhcoder.tool.command.CommandManager;
import dhcoder.tool.javafx.fxutils.FxController;
import javafx.stage.PopupWindow;

/**
 * A dialog which can search through all registered, named actions.
 */
public final class CommandWindow extends PopupWindow {

    private final CommandManager commandManager;

    public CommandWindow(final CommandManager commandManager) {
        this.commandManager = commandManager;

        autoHideProperty().set(true);

        CommandWindowController controller = FxController.load(CommandWindowController.class);
        controller.setCommandWindow(this);
        getScene().setRoot(controller.getRoot());
    }

    public CommandManager getCommandManager() {
        return commandManager;
    }
}
