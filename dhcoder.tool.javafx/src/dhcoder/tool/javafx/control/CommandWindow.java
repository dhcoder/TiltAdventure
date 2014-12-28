package dhcoder.tool.javafx.control;

import dhcoder.tool.javafx.fxutils.FxController;
import javafx.stage.PopupWindow;
import org.controlsfx.control.action.ActionGroup;

/**
 * A dialog which can search through all registered, named actions.
 */
public final class CommandWindow extends PopupWindow {

    private final ActionGroup allActions;

    public CommandWindow() {
        allActions = new ActionGroup("");

        autoHideProperty().set(true);

        CommandWindowController controller = FxController.load(CommandWindowController.class);
        controller.setCommandWindow(this);
        getScene().setRoot(controller.getRoot());
    }

    public ActionGroup getAllActions() {
        return allActions;
    }
}
