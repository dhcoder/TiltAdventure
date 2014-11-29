package tiltadv.tools.scene;

import com.badlogic.gdx.Gdx;
import dhcoder.libgdx.tool.command.Command;
import dhcoder.libgdx.tool.command.CommandManager;
import dhcoder.libgdx.tool.command.CommandScope;
import dhcoder.libgdx.tool.scene2d.widget.CommandTree;
import dhcoder.libgdx.tool.scene2d.widget.CommandWindow;

/**
 * Commands that should get executed anytime.
 */
public final class GlobalCommands {

    public final CommandScope globalScope;
    public final CommandScope helpScope;
    public final CommandScope fileScope;
    public final CommandScope editScope;
    public final Command showCommandWindow;
    public final Command newScene;
    public final Command closeScene;
    public final Command exit;
    public final Command undo;
    public final Command redo;
    public final Command listAllCommands;

    public GlobalCommands(final SceneTool sceneTool, final CommandManager commandManager) {
        globalScope = new CommandScope("Global", true);
        helpScope = new CommandScope("Help", globalScope);
        fileScope = new CommandScope("File", globalScope);
        editScope = new CommandScope("Edit", globalScope);

        showCommandWindow = new Command("show_command_window", globalScope, "Show Command Window",
            "Opens the Command Window which allows for searching all commands", new Command.RunCallback() {
            @Override
            public void run() {
                final CommandWindow commandWindow = sceneTool.getCommandWindow();
                commandWindow.show();
            }
        });
        commandManager.excludeFromSearch(showCommandWindow);

        newScene = new Command("new_scene", fileScope, "New Scene", "Opens a new, blank scene to work on",
            new Command.RunCallback() {
                @Override
                public void run() {

                }
            });

        closeScene =
            new Command("close_scene", fileScope, "Close Scene", "Closes the current scene", new Command.RunCallback() {
                @Override
                public void run() {

                }
            });

        exit = new Command("exit", fileScope, "Exit", "Exits the application", new Command.RunCallback() {
            @Override
            public void run() {
                Gdx.app.exit();
            }
        });

        undo = new Command("undo", editScope, "Undo", "Undo your last action", new Command.RunCallback() {
            @Override
            public void run() {

            }
        });

        redo = new Command("redo", editScope, "Redo", "Redo your last undone action", new Command.RunCallback() {
            @Override
            public void run() {

            }
        });

        listAllCommands =
            new Command("toggle_command_tree", helpScope, "Show/Hide Command Tree", "Toggles the command tree window",
                new Command.RunCallback() {
                    @Override
                    public void run() {
                        CommandTree commandTree = sceneTool.getCommandTree();
                        commandTree.setVisible(!commandTree.isVisible());
                    }
                });

        commandManager.register(globalScope);
    }
}
