package tiltadv.tools.scene.action;

import com.badlogic.gdx.Input.Keys;
import dhcoder.libgdx.tool.action.Action;
import dhcoder.libgdx.tool.action.ActionManager;
import dhcoder.libgdx.tool.action.Shortcut;

import java.lang.reflect.Field;

/**
 * A list of all actions used by this tool.
 */
public final class Actions {

    public static final Action ShowActionWindow =
        new Action("show_action_window", Scopes.Global, "Show Action Window",
            "Opens the Action toolbar which lets you enter a command", new Action.RunCallback() {
            @Override
            public void run() {

            }
        });

    public static final Action NewScene =
        new Action("new_scene", Scopes.File, "New Scene", "Opens a new, blank scene to work on",
            new Action.RunCallback() {
                @Override
                public void run() {

                }
            });

    public static final Action CloseScene =
        new Action("close_scene", Scopes.File, "Close Scene", "Closes the current scene", new Action.RunCallback() {
            @Override
            public void run() {

            }
        });

    public static final Action Exit =
        new Action("exit", Scopes.File, "Exit", "Exits the application", new Action.RunCallback() {
            @Override
            public void run() {

            }
        })

    public static final Action Undo =
        new Action("undo", Scopes.Edit, "Undo", "Undo your last action", new Action.RunCallback() {
            @Override
            public void run() {

            }
        });

    public static final Action Redo =
        new Action("redo", Scopes.Edit, "Redo", "Redo your last undone action", new Action.RunCallback() {
            @Override
            public void run() {

            }
        });

    public static final Action ListAllActions = new Action("list_all_actions", Scopes.Help, "List All Actions",
        "Shows all actions in a window", new Action.RunCallback() {
        @Override
        public void run() {

        }
    });

    public static void registerWith(final ActionManager actionManager) {
        for (Field field : Actions.class.getFields()) {
            try {
                actionManager.register((Action)field.get(null));
            } catch (IllegalAccessException e) {
                throw new RuntimeException("Unexpected exception when registering commands");
            }
        }
        actionManager.excludeFromSearch(ShowActionWindow);
    }

    static {
        ShowActionWindow.setShortcut(Shortcut.ctrlShift(Keys.A));
    }

    private Actions() {} // Static class
}
