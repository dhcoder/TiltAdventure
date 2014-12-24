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
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.Window;
import tiltadv.tools.scene.serialization.SettingsLoader;
import tiltadv.tools.scene.view.NoSceneController;

import java.io.IOException;

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
    private Node sceneView;

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
        try {
            rootPane.getChildren().add(loadNoSceneView());
            sceneView = loadSceneView();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Scene scene = new Scene(rootPane, appSettings.getWidth(), appSettings.getHeight());
        stage.setScene(scene);

        CommandListener listener = new CommandListener(globalCommands.globalScope);
        listener.install(scene);

        stage.show();
    }

    public void newScene() {
        if (!rootPane.getChildren().contains(sceneView)) {
            rootPane.getChildren().add(sceneView);
        }
    }

    public void closeScene() {
        //TODO: Close tab, and remove sceneview if closing the last one
        rootPane.getChildren().remove(sceneView);
    }

    public void showCommandWindow() {
        Window window = getStage().getScene().getWindow();
        double windowX = window.getX();
        double windowY = window.getY();
        commandWindow.show(getStage());
        commandWindow.setX(windowX + (window.getWidth() - commandWindow.getWidth()) / 2);
        commandWindow.setY(windowY + 50);
    }

    private Node loadNoSceneView() throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(SceneTool.class.getResource("view/NoSceneView.fxml"));
        Node noSceneView = loader.load();

        NoSceneController controller = loader.getController();
        controller.setCommandWindowCommand(globalCommands.showCommandWindow, globalCommands.newScene);

        return noSceneView;
    }

    private Node loadSceneView() throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(SceneTool.class.getResource("view/SceneView.fxml"));
        Node sceneView = loader.load();

        return sceneView;
    }

    private void loadShortcuts(final Json json, final CommandManager commandManager) {
        ShortcutsLoader.load(json, commandManager, PATH_CONFIG + "shortcuts.json");
    }
}