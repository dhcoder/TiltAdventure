package dhcoder.libgdx.tool.command;

import com.badlogic.gdx.Input.Keys;

/**
 * Class that handles input and fires the appropriate {@link Shortcut}s.
 */
public final class InputHandler {
    private final CommandManager commandManager;
    private boolean ctrl;
    private boolean alt;
    private boolean shift;

    public InputHandler(final CommandManager commandManager) {
        this.commandManager = commandManager;
    }

    public boolean handleKeyDown(final int key) {
        if (key == Keys.CONTROL_LEFT || key == Keys.CONTROL_RIGHT) {
            ctrl = true;
        }
        else if (key == Keys.ALT_LEFT || key == Keys.ALT_RIGHT) {
            alt = true;
        }
        else if (key == Keys.SHIFT_LEFT || key == Keys.SHIFT_RIGHT) {
            shift = true;
        }
        else {
            Shortcut shortcut = new Shortcut(ctrl, alt, shift, key);
            return commandManager.handle(shortcut);
        }

        return false;
    }

    public boolean handleKeyUp(final int key) {
        if (key == Keys.CONTROL_LEFT || key == Keys.CONTROL_RIGHT) {
            ctrl = false;
        }
        else if (key == Keys.ALT_LEFT || key == Keys.ALT_RIGHT) {
            alt = false;
        }
        else if (key == Keys.SHIFT_LEFT || key == Keys.SHIFT_RIGHT) {
            shift = false;
        }

        return false;
    }
}
