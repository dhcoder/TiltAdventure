package tiltadv.tools.scene;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglFiles;
import com.badlogic.gdx.utils.Json;
import dhcoder.tool.command.CommandManager;
import dhcoder.tool.command.Shortcut;
import dhcoder.tool.libgdx.serialization.ShortcutsLoader;
import dhcoder.tool.swing.command.SwingKeyNameProvider;
import dhcoder.tool.swing.widget.CommandWindow;
import dhcoder.tool.swing.widget.OverlapPane;
import tiltadv.tools.scene.forms.FirstRunForm;
import tiltadv.tools.scene.serialization.SettingsLoader;
import tiltadv.tools.scene.serialization.SettingsLoader.AppSettings;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;

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
                sceneTool.run(1280, 720);
            }
        });
    }

    private final GlobalCommands globalCommands;

    private OverlapPane overlapPane;
    private CommandWindow commandWindow;

    static {
        Gdx.files = new LwjglFiles();
    }

    private JPanel panelRoot;

    public SceneTool() throws HeadlessException {
        super("Scene Editor");

        Shortcut.setKeyNameProvider(new SwingKeyNameProvider());
        setContentPane(panelRoot);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        overlapPane = new OverlapPane();
        panelRoot.add(overlapPane, BorderLayout.CENTER);

        Json json = new Json();
        AppSettings appSettings = SettingsLoader.load(json, PATH_CONFIG + "settings.json");

        CommandManager commandManager = new CommandManager();
        globalCommands = new GlobalCommands(this, commandManager);
        loadShortcuts(json, commandManager);

//        labelCommandTree.setText(format("Once open, try searching for \"{0}\" and press <ENTER>",
//            globalCommands.toggleCommandTree.getName()));

        commandWindow = new CommandWindow(this, commandManager);
        commandWindow.setBounds(0, 0, 400, 600);
        commandWindow.setBackground(Color.RED);

        FirstRunForm firstRunForm = new FirstRunForm(globalCommands);
        overlapPane.addComponent(firstRunForm.getPanelRoot());
//        JPanel test = new JPanel();
//        test.setBackground(Color.ORANGE);
//        JLabel testLabel = new JLabel("DUMMY");
//        test.add(testLabel);
//        overlapPane.addComponent(test);
//        layeredPane.add(commandWindow, layer++);

//        commandWindow = new CommandWindow(commandManager, skin);
//        commandWindow.setVisible(false);
//        commandWindowTable.add(commandWindow).expandY().fillY().width(400f).fillX().top().pad(20f, 0f, 0f, 0f);
//
//        commandTree = new CommandTree(commandManager, skin);
//        commandTree.setVisible(false);
//        stage.addActor(commandTree);

        rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
            .put(KeyStroke.getKeyStroke('\\', InputEvent.CTRL_DOWN_MASK), "show_command_window");
        rootPane.getActionMap().put("show_command_window", new AbstractAction() {
            @Override
            public void actionPerformed(final ActionEvent actionEvent) {
                commandWindow.setLocationRelativeTo(SceneTool.this);
                commandWindow.setVisible(true);
            }
        });
    }

    public void run(final int width, final int height) {
        pack();
        setSize(width, height);
        setVisible(true);
    }

    public CommandWindow getCommandWindow() {
        return commandWindow;
    }

    private void loadShortcuts(final Json json, final CommandManager commandManager) {
        ShortcutsLoader.load(json, commandManager, PATH_CONFIG + "shortcuts.json");
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
    }

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        panelRoot = new JPanel();
        panelRoot.setLayout(new BorderLayout(0, 0));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() { return panelRoot; }
}
