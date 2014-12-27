package dhcoder.tool.command;

import dhcoder.support.text.StringUtils;

import java.text.ParseException;

import static dhcoder.support.text.StringUtils.format;

/**
 * Keyboard shortcut helper class.
 */
public final class Shortcut {

    public static class Builder {
        final int key;
        boolean ctrl;
        boolean cmd;
        boolean alt;
        boolean shift;

        public Builder(final int key) {this.key = key;}

        public Builder ctrl() {
            ctrl = true;
            return this;
        }

        public Builder cmd() {
            cmd = true;
            return this;
        }

        public Builder alt() {
            alt = true;
            return this;
        }

        public Builder shift() {
            shift = true;
            return this;
        }

        public Shortcut build() {
            return new Shortcut(ctrl, cmd, alt, shift, key);
        }
    }

    private static boolean IS_MAC = System.getProperty("os.name").startsWith("Mac OS");
    public static boolean TREAT_CMD_AS_CTRL = !IS_MAC;
    private static KeyNameProvider keyNameProvider;

    public static Builder of(final int key) {
        return new Builder(key);
    }

    public static Shortcut fromString(final String shortcutString) throws ParseException {
        if (StringUtils.isWhitespace(shortcutString)) {
            throw new ParseException(shortcutString, 0);
        }

        String[] shortcutParts = shortcutString.split("\\+");
        if (shortcutParts.length == 0) {
            throw new ParseException(shortcutString, 0);
        }

        boolean ctrl = false, cmd = false, alt = false, shift = false;
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
            else if (modifierPart.equals("Cmd")) {
                if (TREAT_CMD_AS_CTRL) {
                    ctrl = true;
                }
                else {
                    cmd = true;
                }
            }
            else {
                throw new ParseException(shortcutString, position);
            }
            position += modifierPart.length() + 1;
        }

        String keyPart = shortcutParts[shortcutParts.length - 1];
        int key = getKeyNameProvider().getKey(keyPart);

        if (key == -1) {
            throw new ParseException(shortcutString, position);
        }

        return new Shortcut(ctrl, cmd, alt, shift, key);
    }

    private static KeyNameProvider getKeyNameProvider() {
        if (keyNameProvider == null) {
            throw new IllegalStateException("You must call setKeyNameProvider before using this class.");
        }
        return keyNameProvider;
    }

    public static void setKeyNameProvider(final KeyNameProvider keyNameProvider) {
        Shortcut.keyNameProvider = keyNameProvider;
    }

    private final boolean ctrl;
    private final boolean alt;
    private final boolean shift;
    private final boolean cmd;
    private final int key;

    public Shortcut(final boolean ctrl, final boolean cmd, final boolean alt, final boolean shift, final int key) {
        if (TREAT_CMD_AS_CTRL) {
            this.ctrl = ctrl || cmd;
            this.cmd = false;
        }
        else {
            this.ctrl = ctrl;
            this.cmd = cmd;
        }
        this.alt = alt;
        this.shift = shift;
        this.key = key;
    }

    @Override
    public int hashCode() {
        int result = (ctrl ? 1 : 0);
        result = 31 * result + (cmd ? 1 : 0);
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

        if (ctrl != shortcut.ctrl) { return false; }
        if (cmd != shortcut.cmd) { return false; }
        if (alt != shortcut.alt) { return false; }
        if (shift != shortcut.shift) { return false; }
        if (key != shortcut.key) { return false; }

        return true;
    }

    @Override
    public String toString() {
        return format("{0}{1}{2}{3}{4}", ctrl ? "Ctrl+" : "", cmd ? "Cmd+" : "", alt ? "Alt+" : "",
            shift ? "Shift+" : "", getKeyNameProvider().getName(key));
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

    public boolean cmd() { return cmd; }

    public int key() {
        return key;
    }
}
