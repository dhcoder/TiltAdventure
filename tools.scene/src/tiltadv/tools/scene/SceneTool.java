package tiltadv.tools.scene;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglFiles;
import com.badlogic.gdx.utils.Json;
import dhcoder.libgdx.tool.command.CommandManager;
import dhcoder.libgdx.tool.command.serialization.ShortcutsLoader;
import dhcoder.libgdx.tool.scene2d.widget.CommandTree;
import dhcoder.libgdx.tool.swing.widget.CommandWindow;
import dhcoder.libgdx.tool.swing.widget.OverlapPane;
import tiltadv.tools.scene.serialization.SettingsLoader;
import tiltadv.tools.scene.serialization.SettingsLoader.AppSettings;

import javax.swing.*;
import java.awt.*;

/**
 * Main class for the scene tool. Acts as a collection of all high level UI elements and components, as well
 * as being the entry point of the application.
 */
public final class SceneTool extends JFrame {

    private static final String PATH_CONFIG = "config/scene/";

    public static void main(final String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                SceneTool sceneTool = new SceneTool();
                sceneTool.run();
                sceneTool.setSize(1280, 720);
            }
        });
    }

    private final GlobalCommands globalCommands;

    private OverlapPane overlapPane;
    private CommandWindow commandWindow;

    static {
        Gdx.files = new LwjglFiles();
    }

    public SceneTool() throws HeadlessException {
        super("Scene Editor");
        overlapPane = new OverlapPane();
//        setContentPane(overlapPane);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        Json json = new Json();
        AppSettings appSettings = SettingsLoader.load(json, PATH_CONFIG + "settings.json");

        CommandManager commandManager = new CommandManager();
        globalCommands = new GlobalCommands(this, commandManager);
        loadShortcuts(json, commandManager);

//        labelCommandTree.setText(format("Once open, try searching for \"{0}\" and press <ENTER>",
//            globalCommands.toggleCommandTree.getName()));

        commandWindow = new CommandWindow(commandManager);

//        layeredPane.add(new ScenePanel(), layer++);
        overlapPane.addComponent(new FirstRunPanel(globalCommands));
        setContentPane(new FirstRunPanel(globalCommands));
//        layeredPane.add(commandWindow, layer++);

//        commandWindow = new CommandWindow(commandManager, skin);
//        commandWindow.setVisible(false);
//        commandWindowTable.add(commandWindow).expandY().fillY().width(400f).fillX().top().pad(20f, 0f, 0f, 0f);
//
//        commandTree = new CommandTree(commandManager, skin);
//        commandTree.setVisible(false);
//        stage.addActor(commandTree);
    }

    public void run() {
        pack();
        setVisible(true);
//        commandWindow.showDialog(this);
    }

    public CommandWindow getCommandWindow() {
        return commandWindow;
    }

    public CommandTree getCommandTree() {
        return null;
    }

    private void loadShortcuts(final Json json, final CommandManager commandManager) {
        ShortcutsLoader.load(json, commandManager, PATH_CONFIG + "shortcuts.json");
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
    }
}
