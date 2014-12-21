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
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.Window;
import tiltadv.tools.scene.serialization.SettingsLoader;
import tiltadv.tools.scene.view.NoSceneController;

import javax.swing.*;
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

    private final Opt<Stage> stageOpt = Opt.withNoValue();

    public Stage getStage() {
        return stageOpt.getValue();
    }

    private CommandWindow commandWindow;

    private JPanel panelRoot;

    public SceneTool() {
        Gdx.files = new LwjglFiles();
        Shortcut.setKeyNameProvider(new JFXKeyNameProvider());

        commandManager = new CommandManager();
        globalCommands = new GlobalCommands(this, commandManager);

        commandWindow = new CommandWindow(commandManager);

//        FirstRunForm firstRunForm = new FirstRunForm(globalCommands);
//        overlapPane.addComponent(firstRunForm.getPanelRoot());

//        CommandListener commandListener = new CommandListener(globalCommands.globalScope);
//        commandListener.registerUmbrellaListener(rootPane);

    }

    @Override
    public void stop() {
        stageOpt.clear();
    }

    @Override
    public void start(final Stage stage) {
        stageOpt.set(stage);

        Json json = new Json();
        SettingsLoader.AppSettings appSettings = SettingsLoader.load(json, PATH_CONFIG + "settings.json");
        loadShortcuts(json, commandManager);

        stage.setTitle("Scene Editor");

        StackPane rootPane = new StackPane();

        try {
            // Load person overview.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(SceneTool.class.getResource("view/NoSceneView.fxml"));
            Pane noSceneView = loader.load();

            rootPane.getChildren().add(noSceneView);

            // Give the controller access to the main app.
            NoSceneController controller = loader.getController();
            controller.setCommandWindowCommand(globalCommands.showCommandWindow, globalCommands.newScene);

        } catch (IOException e) {
            e.printStackTrace();
        }

        Scene scene = new Scene(rootPane, appSettings.getWidth(), appSettings.getHeight());
        stage.setScene(scene);

        CommandListener listener = new CommandListener(globalCommands.globalScope);
        listener.install(scene);

        stage.show();
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