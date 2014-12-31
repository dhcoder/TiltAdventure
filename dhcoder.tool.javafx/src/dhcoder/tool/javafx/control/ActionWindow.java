package dhcoder.tool.javafx.control;

import dhcoder.tool.javafx.utils.ActionCollection;
import dhcoder.tool.javafx.utils.FxController;
import javafx.stage.PopupWindow;

/**
 * A dialog which can search through all registered, named actions.
 */
public final class ActionWindow extends PopupWindow {

    private final ActionCollection allActions;

    public ActionWindow() {
        allActions = new ActionCollection();

        autoHideProperty().set(true);

        ActionWindowController controller = FxController.loadView(ActionWindowController.class);
        controller.setCommandWindow(this);
        getScene().setRoot(controller.getRoot());
    }

    public ActionCollection getAllActions() {
        return allActions;
    }
}
