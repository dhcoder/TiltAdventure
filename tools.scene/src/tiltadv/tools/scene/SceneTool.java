package tiltadv.tools.scene;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglFiles;
import com.badlogic.gdx.utils.Json;
import dhcoder.support.opt.Opt;
import dhcoder.tool.command.CommandManager;
import dhcoder.tool.command.Shortcut;
import dhcoder.tool.javafx.command.CommandListener;
import dhcoder.tool.javafx.command.JFXKeyNameProvider;
import dhcoder.tool.javafx.control.CommandWindow;
import dhcoder.tool.libgdx.serialization.ShortcutsLoader;
import javafx.application.Application;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.Window;
import tiltadv.tools.scene.serialization.SettingsLoader;
import tiltadv.tools.scene.view.NoSceneController;
import tiltadv.tools.scene.view.SceneController;

import static dhcoder.tool.javafx.fxutils.FxController.load;

/**
 * Main class for the scene tool. Acts as a collection of all high level UI elements and components, as well
 * as being the entry point of the application.
 */
public final class SceneTool extends Application {

    private static final String PATH_CONFIG = "config/scene/";

    public static void main(final String[] args) {
        Application.launch(args);
    }

    private final CommandManager commandManager;
    private final GlobalCommands globalCommands;
    private final Opt<SceneContext> contextOpt = Opt.withNoValue();
    private final CommandWindow commandWindow;

    private Stage stage;
    private StackPane rootPane;
    private SceneController sceneController;

    public SceneTool() {
        Gdx.files = new LwjglFiles();
        Shortcut.setKeyNameProvider(new JFXKeyNameProvider());

        commandManager = new CommandManager();
        globalCommands = new GlobalCommands(this, commandManager);

        commandWindow = new CommandWindow(commandManager);
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
        loadShortcuts(json, commandManager);

        stage.setTitle("Scene Editor");

        rootPane = new StackPane();
        NoSceneController noSceneController = load(NoSceneController.class);
        sceneController = load(SceneController.class);

        noSceneController.setCommandWindowCommand(globalCommands.showCommandWindow, globalCommands.newScene);
        rootPane.getChildren().add(noSceneController.getRoot());

        Scene scene = new Scene(rootPane, appSettings.getWidth(), appSettings.getHeight());
        stage.setScene(scene);

        CommandListener listener = new CommandListener(globalCommands.globalScope);
        listener.install(scene);

        stage.show();
    }

    public static int TEST_VALUE = 1;
    public void newScene() {
        Node sceneView = sceneController.getRoot();
        if (!rootPane.getChildren().contains(sceneView)) {
            rootPane.getChildren().add(sceneView);
        }

        sceneController.addScene(null, "Test " + TEST_VALUE, event -> {
            if (sceneController.getScenes().size() == 0) {
                rootPane.getChildren().remove(sceneView);
            }
        });
        TEST_VALUE++;
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

    private void loadShortcuts(final Json json, final CommandManager commandManager) {
        ShortcutsLoader.load(json, commandManager, PATH_CONFIG + "shortcuts.json");
    }
}