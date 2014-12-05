package dhcoder.tool.swing.command;

import dhcoder.tool.command.Shortcut;

import javax.swing.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * Register this listener via {@link JComponent#addKeyListener(KeyListener)}
 */
public final class KeystrokeUtils {

    public static KeyStroke fromShortcut(final Shortcut shortcut) {
        int modifiers = 0;
        if (shortcut.ctrl()) {
            modifiers |= InputEvent.CTRL_MASK;
        }
        if (shortcut.shift()) {
            modifiers |= InputEvent.SHIFT_MASK;
        }
        if (shortcut.alt()) {
            modifiers |= InputEvent.ALT_MASK;
        }
        return KeyStroke.getKeyStroke(shortcut.key(), modifiers);
    }

    public static Shortcut toShortcut(final KeyEvent keyEvent) {
        return new Shortcut(keyEvent.isControlDown(), keyEvent.isAltDown(), keyEvent.isShiftDown(),
            keyEvent.getKeyCode());
    }

    private KeystrokeUtils() { }
}
