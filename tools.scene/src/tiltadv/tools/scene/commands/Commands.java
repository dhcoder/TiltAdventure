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

    public static final Command RunAction = new Command("run.action", Scopes.Global, "Run Action",
        "Opens the Action toolbar which lets you enter a command", new Command.RunCallback() {
        @Override
        public void run() {
            int breakhere = 0;
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
    }

    static {
        RunAction.setShortcut(Shortcut.ctrlShift(Keys.A));
    }

    private Commands() {} // Static class
}
