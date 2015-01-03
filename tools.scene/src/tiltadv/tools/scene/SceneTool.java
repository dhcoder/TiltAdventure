package tiltadv.tools.scene;

import com.google.gson.Gson;
import dhcoder.support.opt.Opt;
import dhcoder.tool.game.model.Scene;
import dhcoder.tool.game.model.Tileset;
import dhcoder.tool.game.serialization.TilesetLoader;
import dhcoder.tool.javafx.control.ActionWindow;
import dhcoder.tool.javafx.serialization.ShortcutsLoader;
import dhcoder.tool.javafx.utils.ActionCollection;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.controlsfx.control.action.ActionUtils;
import tiltadv.tools.scene.serialization.SettingsLoader;
import tiltadv.tools.scene.view.NewSceneDialog;
import tiltadv.tools.scene.view.NoSceneController;
import tiltadv.tools.scene.view.ScenesController;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

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
    private final Opt<GameSceneContext> contextOpt = Opt.withNoValue();
    private final Map<Scene, GameSceneContext> sceneContextMap = new HashMap<>();
    private final ActionWindow actionWindow;
    private final Gson gson = new Gson();

    private Stage stage;
    private Parent rootPane;
    private ScenesController scenesController;
    private StackPane appPane;
    private SettingsLoader.AppSettings appSettings;

    public SceneTool() {
        actionWindow = new ActionWindow();
        globalActions = new GlobalActions(this, actionWindow.getAllActions());
    }

    public SettingsLoader.AppSettings getAppSettings() {
        return appSettings;
    }

    public Stage getStage() {
        return stage;
    }

    public Opt<GameSceneContext> getContextOpt() { return contextOpt; }

    public GameSceneContext getContext(final Scene gameScene) {
        return sceneContextMap.get(gameScene);
    }

    @Override
    public void start(final Stage stage) {
        this.stage = stage;

        try {
            appSettings = SettingsLoader.load(gson, Paths.get(PATH_CONFIG, "settings.json"));
        } catch (IOException e) {
            throw new RuntimeException("Can't open app settings!");
        }
        loadShortcuts(gson, actionWindow.getAllActions());

        stage.setTitle("Scene Editor");

        appPane = new StackPane();
        rootPane = new VBox(ActionUtils.createMenuBar(globalActions.globalScope.getActions()), appPane);
        VBox.setVgrow(appPane, Priority.ALWAYS);

        NoSceneController noSceneController = loadView(NoSceneController.class);
        scenesController = loadView(ScenesController.class);
        scenesController.setOnSceneAdding(scene -> {
            if (!appPane.getChildren().contains(scenesController.getRoot())) {
                appPane.getChildren().add(scenesController.getRoot());
            }

            GameSceneContext context = new GameSceneContext(scene);
            sceneContextMap.put(scene, context);
        });
        scenesController.setOnSceneRemoved(scene -> {
            if (scenesController.getSceneCount() == 0) {
                appPane.getChildren().remove(scenesController.getRoot());
                contextOpt.clear();
            }

            sceneContextMap.remove(scene);
        });
        scenesController.setOnSceneSelected(scene -> {
            contextOpt.set(sceneContextMap.get(scene));
        });
        scenesController.setSceneTool(this);

        noSceneController.setTooltipCommands(globalActions.showActionWindow, globalActions.newScene);
        appPane.getChildren().add(noSceneController.getRoot());

        javafx.scene.Scene scene = new javafx.scene.Scene(rootPane, appSettings.getWidth(), appSettings.getHeight());
        stage.setScene(scene);

        stage.show();
    }

    public void newScene() {
        NewSceneDialog newSceneDialog = new NewSceneDialog();
        Opt<NewSceneDialog.Result> resultOpt = newSceneDialog.showAndWait(this);
        if (resultOpt.hasValue()) {
            NewSceneDialog.Result newSceneValues = resultOpt.getValue();
            try {
                Tileset tileset = TilesetLoader.load(gson, appSettings.getAssetDir(), newSceneValues.getTilesetFile());
                scenesController.addScene(new Scene(tileset), newSceneValues.getSceneName());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void closeScene() {
        scenesController.closeActiveScene();
    }

    public void showCommandWindow() {
        Window window = getStage().getScene().getWindow();
        double windowX = window.getX();
        double windowY = window.getY();
        actionWindow.show(getStage());
        actionWindow.setX(windowX + (window.getWidth() - actionWindow.getWidth()) / 2);
        actionWindow.setY(windowY + 50);
    }

    private void loadShortcuts(final Gson gson, final ActionCollection actions) {
        try {
            ShortcutsLoader.load(gson, actions, Paths.get(PATH_CONFIG, "shortcuts.json"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}