package tiltadv.tools.scene.commands;

import com.badlogic.gdx.Input.Keys;
import dhcoder.libgdx.tool.command.Command;
import dhcoder.libgdx.tool.command.CommandManager;
import dhcoder.libgdx.tool.command.Shortcut;

import java.lang.reflect.Field;

/**
 * A list of all commands used by this tool.
 */
public final class Commands {

    public static final Command ShowActionWindow =
        new Command("show_action_window", Scopes.Global, "Show Action Window",
            "Opens the Action toolbar which lets you enter a command", new Command.RunCallback() {
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

    public static final Command ListAllActions = new Command("list_all_actions", Scopes.Help, "List All Actions",
        "Shows all actions in a window", new Command.RunCallback() {
        @Override
        public void run() {

        }
    });

    public static void registerWith(final CommandManager commandManager) {
        for (Field field : Commands.class.getFields()) {
            try {
                commandManager.registerCommand((Command)field.get(null));
            } catch (IllegalAccessException e) {
                throw new RuntimeException("Unexpected exception when registering commands");
            }
        }
        commandManager.excludeFromSearch(ShowActionWindow);
    }

    static {
        ShowActionWindow.setShortcut(Shortcut.ctrlShift(Keys.A));
    }

    private Commands() {} // Static class
}
