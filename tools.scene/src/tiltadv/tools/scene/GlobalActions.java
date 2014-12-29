package tiltadv.tools.scene;

import dhcoder.tool.javafx.utils.ActionBuilder;
import dhcoder.tool.javafx.utils.ActionCollection;
import org.controlsfx.control.action.Action;
import org.controlsfx.control.action.ActionGroup;

/**
 * Actions that can get executed anywhere in the tool, no matter what toolbar has focus.
 */
public final class GlobalActions {

    public final ActionGroup globalScope;
    public final ActionGroup fileScope;
    public final ActionGroup editScope;
    public final ActionGroup viewScope;
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
        viewScope = new ActionGroup("View");

        globalScope.getActions().addAll(fileScope, editScope, viewScope);

        showActionWindow = new ActionBuilder().setId("show_action_window").setParent(viewScope)
            .setText("Show Action Window",
                "Opens the Action Window which allows for searching quickly through all commands")
            .setOnAction(sceneTool::showCommandWindow).build();

        newScene = new ActionBuilder().setId("new_scene").setParent(fileScope)
            .setText("New Scene", "Opens a new, blank scene to work on").setOnAction(sceneTool::newScene).build();

        closeScene = new ActionBuilder().setId("close_scene").setParent(fileScope)
            .setText("Close Scene", "Closes the current scene").setOnAction(sceneTool::closeScene).build();

        exit = new ActionBuilder().setId("exit").setParent(fileScope).setText("Exit", "Exits the application")
            .setOnAction(() -> sceneTool.getStage().close()).build();

        undo = new ActionBuilder().setId("undo").setParent(editScope).setText("Undo", "Undo your last action")
            .setOnAction(() -> sceneTool.getContextOpt().getValue().getHistory().undo()).setActiveTest(
                v -> sceneTool.getContextOpt().hasValue() &&
                    sceneTool.getContextOpt().getValue().getHistory().canUndo()).build();

        redo = new ActionBuilder().setId("redo").setParent(editScope).setText("Redo", "Redo your last action")
            .setOnAction(() -> sceneTool.getContextOpt().getValue().getHistory().redo()).setActiveTest(
                v -> sceneTool.getContextOpt().hasValue() &&
                    sceneTool.getContextOpt().getValue().getHistory().canRedo()).build();

        // No reason to search for the command that brings up the command window when it is already showing
        ActionCollection.setUnsearchable(showActionWindow);

        actionCollection.addAll(globalScope.getActions());
    }
}
