package tiltadv.tools.scene;

import dhcoder.tool.command.Command;
import dhcoder.tool.command.CommandManager;
import dhcoder.tool.command.CommandScope;

/**
 * Commands that can get executed anywhere in the tool, no matter what toolbar has focus.
 */
public final class GlobalCommands {

    public final CommandScope globalScope;
    public final CommandScope fileScope;
    public final CommandScope editScope;
    public final Command showCommandWindow;
    public final Command newScene;
    public final Command closeScene;
    public final Command exit;
    public final Command undo;
    public final Command redo;

    public GlobalCommands(final SceneTool sceneTool, final CommandManager commandManager) {
        globalScope = new CommandScope("Global", true);
        fileScope = new CommandScope("File", globalScope);
        editScope = new CommandScope("Edit", globalScope);

        showCommandWindow = new Command("show_command_window", globalScope, "Show Command Window",
            "Opens the Command Window which allows for searching all commands", sceneTool::showCommandWindow);
        commandManager.excludeFromSearch(showCommandWindow);

        newScene = new Command("new_scene", fileScope, "New Scene", "Opens a new, blank scene to work on",
            sceneTool::newScene);

        closeScene =
            new Command("close_scene", fileScope, "Close Scene", "Closes the current scene", sceneTool::closeScene);

        exit = new Command("exit", fileScope, "Exit", "Exits the application", () -> sceneTool.getStage().close());

        undo = new Command("undo", editScope, "Undo", "Undo your last action",
            () -> sceneTool.getContextOpt().getValue().getHistory().undo()).
            setActiveCallback(() -> sceneTool.getContextOpt().hasValue() &&
                sceneTool.getContextOpt().getValue().getHistory().canUndo());

        redo = new Command("redo", editScope, "Redo", "Redo your last undone action",
            () -> sceneTool.getContextOpt().getValue().getHistory().redo()).
            setActiveCallback(() -> sceneTool.getContextOpt().hasValue() &&
                sceneTool.getContextOpt().getValue().getHistory().canRedo());

        commandManager.register(globalScope);
    }
}
