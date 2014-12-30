package tiltadv.tools.scene;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglFiles;
import com.badlogic.gdx.utils.Json;
import dhcoder.support.opt.Opt;
import dhcoder.tool.javafx.control.CommandWindow;
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
    private final CommandWindow commandWindow;

    private Stage stage;
    private Parent rootPane;
    private SceneController sceneController;
    private StackPane appPane;

    public SceneTool() {
        Gdx.files = new LwjglFiles();

        commandWindow = new CommandWindow();
        globalActions = new GlobalActions(this, commandWindow.getAllActions());
    }

    public Stage getStage() {
        return stage;
    }

    public Opt<SceneContext> getContextOpt() { return contextOpt; }

    @Override
    public void start(final Stage stage) {
        this.stage = stage;

        Json json = new Json();
        SettingsLoader.AppSettings appSettings = SettingsLoader.load(json, PATH_CONFIG + "settings.json");
        loadShortcuts(json, commandWindow.getAllActions());

        stage.setTitle("Scene Editor");

        appPane = new StackPane();
        rootPane = new VBox(
            ActionUtils.createMenuBar(globalActions.globalScope.getActions()), appPane
        );
        VBox.setVgrow(appPane, Priority.ALWAYS);

        NoSceneController noSceneController = loadView(NoSceneController.class);
        sceneController = loadView(SceneController.class);

        noSceneController.setTooltipCommands(globalActions.showActionWindow, globalActions.newScene);
        appPane.getChildren().add(noSceneController.getRoot());

        Scene scene = new Scene(rootPane, appSettings.getWidth(), appSettings.getHeight());
        stage.setScene(scene);

        stage.show();
    }

    public static int TEST_VALUE = 1;
    public void newScene() {
        if (TEST_VALUE == 2) {
            int breakhere =0;
        }
        System.out.println("HERE?");
        NewSceneDialog newSceneDialog = new NewSceneDialog();
        newSceneDialog.showAndWait();
        TEST_VALUE++;
//        Node sceneView = sceneController.getRoot();
//        if (!appPane.getChildren().contains(sceneView)) {
//            appPane.getChildren().add(sceneView);
//        }
//
//        sceneController.addScene(null, "Test " + TEST_VALUE, event -> {
//            if (sceneController.getScenes().size() == 0) {
//                appPane.getChildren().remove(sceneView);
//            }
//        });
//        TEST_VALUE++;
    }

    public void closeScene() {
        //TODO: Close active tab
    }

    public void showCommandWindow() {
        Window window = getStage().getScene().getWindow();
        double windowX = window.getX();
        double windowY = window.getY();
        commandWindow.show(getStage());
        commandWindow.setX(windowX + (window.getWidth() - commandWindow.getWidth()) / 2);
        commandWindow.setY(windowY + 50);
    }

    private void loadShortcuts(final Json json, final ActionCollection actions) {
        ShortcutsLoader.load(json, actions, PATH_CONFIG + "shortcuts.json");
    }
}