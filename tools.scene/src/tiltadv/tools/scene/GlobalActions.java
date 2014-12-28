package tiltadv.tools.scene;

import dhcoder.tool.javafx.utils.ActionCollection;
import dhcoder.tool.javafx.utils.ActionFactory;
import org.controlsfx.control.action.Action;
import org.controlsfx.control.action.ActionGroup;

/**
 * Actions that can get executed anywhere in the tool, no matter what toolbar has focus.
 */
public final class GlobalActions {

    public final ActionGroup globalScope;
    public final ActionGroup fileScope;
    public final ActionGroup editScope;
    public final Action showActionWindow;
    public final Action newScene;
    public final Action closeScene;
    public final Action exit;
    public final Action undo;
    public final Action redo;

    public GlobalActions(final SceneTool sceneTool, final ActionCollection actionCollection) {
        globalScope = new ActionGroup("Global");
        fileScope = new ActionGroup("File");
        editScope = new ActionGroup("Edit");

        globalScope.getActions().addAll(fileScope, editScope);

        showActionWindow = ActionFactory.create("show_action_window", globalScope, "Show Action Window",
            "Opens the Action Window which allows for searching quickly through all commands",
            sceneTool::showCommandWindow);

        newScene =
            ActionFactory.create("new_scene", fileScope, "New Scene", "Opens a new, blank scene to work on", sceneTool::newScene);

        closeScene = ActionFactory.create("close_scene", fileScope, "Close Scene", "Closes the current scene", sceneTool::closeScene);

        exit = ActionFactory.create("exit", fileScope, "Exit", "Exits the application", () -> sceneTool.getStage().close());

        undo = ActionFactory.create("undo", editScope, "Undo", "Undo your last action",
            () -> sceneTool.getContextOpt().getValue().getHistory().undo());
        redo = ActionFactory.create("redo", editScope, "Redo", "Redo your last action",
            () -> sceneTool.getContextOpt().getValue().getHistory().redo());

        // No reason to search for the command that brings up the command window when it is already showing
        ActionCollection.setUnsearchable(showActionWindow);

        actionCollection.addAll(globalScope.getActions());
    }
}
