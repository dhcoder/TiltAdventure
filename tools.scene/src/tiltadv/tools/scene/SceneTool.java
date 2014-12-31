package tiltadv.tools.scene;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglFiles;
import com.badlogic.gdx.utils.Json;
import dhcoder.support.opt.Opt;
import dhcoder.tool.javafx.control.ActionWindow;
import dhcoder.tool.javafx.libgdx.serialization.ShortcutsLoader;
import dhcoder.tool.javafx.utils.ActionCollection;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.controlsfx.control.action.ActionUtils;
import tiltadv.tools.scene.serialization.SettingsLoader;
import tiltadv.tools.scene.view.NewSceneDialog;
import tiltadv.tools.scene.view.NoSceneController;
import tiltadv.tools.scene.view.SceneController;

import static dhcoder.tool.javafx.utils.FxController.loadView;

/**
 * Main class for the scene tool. Acts as a collection of all high level UI elements and components, as well
 * as being the entry point of the application.
 */
public final class SceneTool extends Application {

    private static final String PATH_CONFIG = "config/scene/";

    public static void main(final String[] args) {
        Application.launch(args);
    }

    private final GlobalActions globalActions;
    private final Opt<SceneContext> contextOpt = Opt.withNoValue();
    private final ActionWindow actionWindow;

    private Stage stage;
    private Parent rootPane;
    private SceneController sceneController;
    private StackPane appPane;
    private SettingsLoader.AppSettings appSettings;

    public SceneTool() {
        Gdx.files = new LwjglFiles();

        actionWindow = new ActionWindow();
        globalActions = new GlobalActions(this, actionWindow.getAllActions());
    }

    public SettingsLoader.AppSettings getAppSettings() {
        return appSettings;
    }

    public Stage getStage() {
        return stage;
    }

    public Opt<SceneContext> getContextOpt() { return contextOpt; }

    @Override
    public void start(final Stage stage) {
        this.stage = stage;

        Json json = new Json();
        appSettings = SettingsLoader.load(json, PATH_CONFIG + "settings.json");
        loadShortcuts(json, actionWindow.getAllActions());

        stage.setTitle("Scene Editor");

        appPane = new StackPane();
        rootPane = new VBox(ActionUtils.createMenuBar(globalActions.globalScope.getActions()), appPane);
        VBox.setVgrow(appPane, Priority.ALWAYS);

        NoSceneController noSceneController = loadView(NoSceneController.class);
        sceneController = loadView(SceneController.class);

        noSceneController.setTooltipCommands(globalActions.showActionWindow, globalActions.newScene);
        appPane.getChildren().add(noSceneController.getRoot());

        Scene scene = new Scene(rootPane, appSettings.getWidth(), appSettings.getHeight());
        stage.setScene(scene);

        stage.show();
    }

    public void newScene() {
        NewSceneDialog newSceneDialog = new NewSceneDialog();
        newSceneDialog.showAndWait(this);
    }

    public void closeScene() {
        //TODO: Close active tab
    }

    public void showCommandWindow() {
        Window window = getStage().getScene().getWindow();
        double windowX = window.getX();
        double windowY = window.getY();
        actionWindow.show(getStage());
        actionWindow.setX(windowX + (window.getWidth() - actionWindow.getWidth()) / 2);
        actionWindow.setY(windowY + 50);
    }

    private void loadShortcuts(final Json json, final ActionCollection actions) {
        ShortcutsLoader.load(json, actions, PATH_CONFIG + "shortcuts.json");
    }
}