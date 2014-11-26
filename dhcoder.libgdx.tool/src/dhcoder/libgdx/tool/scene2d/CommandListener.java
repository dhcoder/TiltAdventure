package dhcoder.libgdx.tool.scene2d;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import dhcoder.libgdx.tool.command.Command;
import dhcoder.libgdx.tool.command.CommandScope;
import dhcoder.libgdx.tool.command.Shortcut;

/**
 * A scene2D {@link InputListener} that listens to {@link Command}s under a particular {@link CommandScope}.
 */
public final class CommandListener extends InputListener {
    private final CommandScope commandScope;
    private boolean ctrl;
    private boolean alt;
    private boolean shift;

    public CommandListener(final CommandScope commandScope) {
        this.commandScope = commandScope;
    }

    @Override
    public boolean keyDown(final InputEvent event, final int keycode) {
        if (keycode == Keys.CONTROL_LEFT || keycode == Keys.CONTROL_RIGHT) {
            ctrl = true;
        }
        else if (keycode == Keys.ALT_LEFT || keycode == Keys.ALT_RIGHT) {
            alt = true;
        }
        else if (keycode == Keys.SHIFT_LEFT || keycode == Keys.SHIFT_RIGHT) {
            shift = true;
        }
        else {
            Shortcut shortcut = new Shortcut(ctrl, alt, shift, keycode);
            return commandScope.handle(shortcut);
        }

        return false;
    }

    @Override
    public boolean keyUp(final InputEvent event, final int keycode) {
        if (keycode == Keys.CONTROL_LEFT || keycode == Keys.CONTROL_RIGHT) {
            ctrl = false;
        }
        else if (keycode == Keys.ALT_LEFT || keycode == Keys.ALT_RIGHT) {
            alt = false;
        }
        else if (keycode == Keys.SHIFT_LEFT || keycode == Keys.SHIFT_RIGHT) {
            shift = false;
        }

        return false;
    }
}
