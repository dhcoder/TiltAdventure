package dhcoder.libgdx.tool.command;

import com.badlogic.gdx.Input;
import dhcoder.support.text.StringUtils;

import java.text.ParseException;

import static dhcoder.support.text.StringUtils.format;

/**
 * Keyboard shortcut helper class.
 */
public final class Shortcut {
    public static Shortcut noModifier(final int key) {
        return new Shortcut(false, false, false, key);
    }

    public static Shortcut ctrl(final int key) {
        return new Shortcut(true, false, false, key);
    }

    public static Shortcut alt(final int key) {
        return new Shortcut(false, true, false, key);
    }

    public static Shortcut shift(final int key) {
        return new Shortcut(false, false, true, key);
    }

    public static Shortcut ctrlAlt(final int key) {
        return new Shortcut(true, true, false, key);
    }

    public static Shortcut ctrlShift(final int key) {
        return new Shortcut(true, false, true, key);
    }

    public static Shortcut altShift(final int key) {
        return new Shortcut(false, true, true, key);
    }

    public static Shortcut ctrlAltShift(final int key) {
        return new Shortcut(true, true, true, key);
    }

    public static Shortcut fromString(final String shortcutString) throws ParseException {
        if (StringUtils.isWhitespace(shortcutString)) {
            throw new ParseException(shortcutString, 0);
        }

        String[] shortcutParts = shortcutString.split("\\+");
        if (shortcutParts.length == 0) {
            throw new ParseException(shortcutString, 0);
        }

        boolean ctrl = false, alt = false, shift = false;
        int position = 0;
        for (int i = 0; i < shortcutParts.length - 1; i++) {
            String modifierPart = shortcutParts[i];
            if (modifierPart.equals("Ctrl")) {
                ctrl = true;
            }
            else if (modifierPart.equals("Alt")) {
                alt = true;
            }
            else if (modifierPart.equals("Shift")) {
                shift = true;
            }
            else {
                throw new ParseException(shortcutString, position);
            }
            position += modifierPart.length() + 1;
        }

        String keyPart = shortcutParts[shortcutParts.length - 1];
        int key = Input.Keys.valueOf(keyPart);

        if (key == -1) {
            throw new ParseException(shortcutString, position);
        }

        return new Shortcut(ctrl, alt, shift, key);
    }

    private final boolean ctrl;
    private final boolean alt;
    private final boolean shift;
    private final int key;

    /**
     * @param key Set to a value in {@link Input.Keys}
     */
    public Shortcut(final boolean ctrl, final boolean alt, final boolean shift, final int key) {
        this.shift = shift;
        this.ctrl = ctrl;
        this.alt = alt;
        this.key = key;
    }

    @Override
    public int hashCode() {
        int result = (ctrl ? 1 : 0);
        result = 31 * result + (alt ? 1 : 0);
        result = 31 * result + (shift ? 1 : 0);
        result = 31 * result + key;
        return result;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }

        Shortcut shortcut = (Shortcut)o;

        if (alt != shortcut.alt) { return false; }
        if (ctrl != shortcut.ctrl) { return false; }
        if (key != shortcut.key) { return false; }
        if (shift != shortcut.shift) { return false; }

        return true;
    }

    @Override
    public String toString() {
        return format("{0}{1}{2}{3}", ctrl ? "Ctrl+" : "", alt ? "Alt+" : "", shift ? "Shift+" : "",
            Input.Keys.toString(key));
    }

    public boolean ctrl() {
        return ctrl;
    }

    public boolean alt() {
        return alt;
    }

    public boolean shift() {
        return shift;
    }

    public int key() {
        return key;
    }
}
