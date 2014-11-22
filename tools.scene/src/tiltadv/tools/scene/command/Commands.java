package tiltadv.tools.scene.command;

import dhcoder.libgdx.tool.command.Command;
import dhcoder.libgdx.tool.command.CommandManager;

import java.lang.reflect.Field;

/**
 * A list of all commands used by this tool.
 */
public final class Commands {

    public static final Command SHOW_COMMAND_WINDOW =
        new Command("show_command_window", Scopes.Global, "Show Command Window",
            "Opens the Command Window which allows for searching all commands", new Command.RunCallback() {
            @Override
            public void run() {

            }
        });

    public static final Command NewScene =
        new Command("new_scene", Scopes.File, "New Scene", "Opens a new, blank scene to work on",
            new Command.RunCallback() {
                @Override
                public void run() {

                }
            });

    public static final Command CloseScene =
        new Command("close_scene", Scopes.File, "Close Scene", "Closes the current scene", new Command.RunCallback() {
            @Override
            public void run() {

            }
        });

    public static final Command Exit =
        new Command("exit", Scopes.File, "Exit", "Exits the application", new Command.RunCallback() {
            @Override
            public void run() {

            }
        });

    public static final Command Undo =
        new Command("undo", Scopes.Edit, "Undo", "Undo your last action", new Command.RunCallback() {
            @Override
            public void run() {

            }
        });

    public static final Command Redo =
        new Command("redo", Scopes.Edit, "Redo", "Redo your last undone action", new Command.RunCallback() {
            @Override
            public void run() {

            }
        });

    public static final Command LIST_ALL_COMMANDS =
        new Command("list_all_actions", Scopes.Help, "List All Actions", "Shows all actions in a window",
            new Command.RunCallback() {
                @Override
                public void run() {

                }
            });

    public static void registerWith(final CommandManager commandManager) {
        for (Field field : Commands.class.getFields()) {
            try {
                commandManager.register((Command)field.get(null));
            } catch (IllegalAccessException e) {
                throw new RuntimeException("Unexpected exception when registering commands");
            }
        }
        commandManager.excludeFromSearch(SHOW_COMMAND_WINDOW);
    }

    private Commands() {} // Static class
}
