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
    public final ActionGroup zoomScope;
    public final Action showActionWindow;
    public final Action newScene;
    public final Action closeScene;
    public final Action exit;
    public final Action undo;
    public final Action redo;
    public final Action increaseZoom;
    public final Action decreaseZoom;
    public final Action setZoom1;
    public final Action setZoom2;
    public final Action setZoom3;
    public final Action setZoom4;

    public GlobalActions(final SceneTool sceneTool, final ActionCollection actionCollection) {
        globalScope = new ActionGroup("Global");
        fileScope = new ActionGroup("File");
        editScope = new ActionGroup("Edit");
        viewScope = new ActionGroup("View");
        zoomScope = new ActionGroup("Zoom");

        globalScope.getActions().addAll(fileScope, editScope, viewScope);

        showActionWindow = new ActionBuilder().setId("show_action_window").setParent(viewScope)
            .setText("Show Action Window",
                "Opens the Action Window which allows for searching quickly through all commands")
            .setHandler(sceneTool::showCommandWindow).build();

        newScene = new ActionBuilder().setId("new_scene").setParent(fileScope)
            .setText("New Scene", "Opens a new, blank scene to work on").setHandler(sceneTool::newScene).build();

        closeScene = new ActionBuilder().setId("close_scene").setParent(fileScope)
            .setText("Close Scene", "Closes the current scene").setHandler(sceneTool::closeScene)
            .setIsActive(() -> sceneTool.getContextOpt().hasValue()).build();

        exit = new ActionBuilder().setId("exit").setParent(fileScope).setText("Exit", "Exits the application")
            .setHandler(() -> sceneTool.getStage().close()).build();

        undo = new ActionBuilder().setId("undo").setParent(editScope).setText("Undo", "Undo your last action")
            .setHandler(() -> sceneTool.getContextOpt().getValue().getHistory().undo()).setIsActive(
                () -> sceneTool.getContextOpt().hasValue() &&
                    sceneTool.getContextOpt().getValue().getHistory().canUndo()).build();

        redo = new ActionBuilder().setId("redo").setParent(editScope).setText("Redo", "Redo your last action")
            .setHandler(() -> sceneTool.getContextOpt().getValue().getHistory().redo()).setIsActive(
                () -> sceneTool.getContextOpt().hasValue() &&
                    sceneTool.getContextOpt().getValue().getHistory().canRedo()).build();

        viewScope.getActions().add(zoomScope);

        increaseZoom = new ActionBuilder().setId("zoom+").setParent(zoomScope)
            .setText("Zoom In", "Increase the global zoom factor")
            .setHandler(() -> sceneTool.getAppSettings().setZoomFactor(sceneTool.getAppSettings().getZoomFactor()))
            .build();

        decreaseZoom = new ActionBuilder().setId("zoom-").setParent(zoomScope)
            .setText("Zoom Out", "Decrease the global zoom factor")
            .setHandler(() -> sceneTool.getAppSettings().setZoomFactor(sceneTool.getAppSettings().getZoomFactor()))
            .build();

        setZoom1 = new ActionBuilder().setId("zoom1").setParent(zoomScope)
            .setText("Set Zoom x1", "Set the global zoom factor to no zoom")
            .setHandler(() -> sceneTool.getAppSettings().setZoomFactor(1)).build();
        setZoom2 = new ActionBuilder().setId("zoom2").setParent(zoomScope)
            .setText("Set Zoom x2", "Set the global zoom factor to 2x zoom")
            .setHandler(() -> sceneTool.getAppSettings().setZoomFactor(2)).build();
        setZoom3 = new ActionBuilder().setId("zoom3").setParent(zoomScope)
            .setText("Set Zoom x3", "Set the global zoom factor to 3x zoom")
            .setHandler(() -> sceneTool.getAppSettings().setZoomFactor(3)).build();
        setZoom4 = new ActionBuilder().setId("zoom4").setParent(zoomScope)
            .setText("Set Zoom x4", "Set the global zoom factor to 4x zoom")
            .setHandler(() -> sceneTool.getAppSettings().setZoomFactor(4)).build();

        // No reason to search for the command that brings up the action window when it is already showing
        ActionCollection.setUnsearchable(showActionWindow);

        actionCollection.addAll(globalScope.getActions());
    }
}
