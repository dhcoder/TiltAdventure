package dhcoder.tool.javafx.command;

import dhcoder.tool.command.Shortcut;
import javafx.scene.input.KeyEvent;

public final class KeyUtils {

    public static Shortcut toShortcut(final KeyEvent keyEvent) {
        return new Shortcut(keyEvent.isControlDown(), keyEvent.isMetaDown(), keyEvent.isAltDown(), keyEvent.isShiftDown(),
            keyEvent.getCode().ordinal());
    }

    private KeyUtils() { }
}
