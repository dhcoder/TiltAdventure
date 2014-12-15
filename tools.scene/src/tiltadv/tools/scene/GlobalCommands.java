package tiltadv.tools.scene;

import dhcoder.tool.command.Command;
import dhcoder.tool.command.CommandManager;
import dhcoder.tool.command.CommandScope;

//import dhcoder.tool.scene2d.widget.CommandTree;

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
//    public final Command toggle_command_tree;

    public GlobalCommands(final SceneTool sceneTool, final CommandManager commandManager) {
        globalScope = new CommandScope("Global", true);
        fileScope = new CommandScope("File", globalScope);
        editScope = new CommandScope("Edit", globalScope);
        helpScope = new CommandScope("Help", globalScope);

        showCommandWindow = new Command("show_command_window", globalScope, "Show Command Window",
            "Opens the Command Window which allows for searching all commands", new Command.RunCallback() {
            @Override
            public void run() {
                sceneTool.getCommandWindow().show(sceneTool.getStage());
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
                sceneTool.getStage().close();
            }
        });

        undo = new Command("undo", editScope, "Undo", "Undo your last action", new Command.RunCallback() {
            @Override
            public void run() {

            }
        }).setActiveCallback(new Command.ActiveCallback() {
            @Override
            public boolean isActive() {
                return false;
            }
        });

        redo = new Command("redo", editScope, "Redo", "Redo your last undone action", new Command.RunCallback() {
            @Override
            public void run() {

            }
        });

//        toggleCommandTree =
//            new Command("toggle_command_tree", helpScope, "Show/Hide Command Tree", "Toggles the command tree window",
//                new Command.RunCallback() {
//                    @Override
//                    public void run() {
//                        CommandTree commandTree = sceneTool.getCommandTree();
//                        commandTree.setVisible(!commandTree.isVisible());
//                    }
//                });

        commandManager.register(globalScope);
    }
}
