package dhcoder.libgdx.tool.command;

import com.badlogic.gdx.Input;
import dhcoder.support.text.StringUtils;

/**
 * Keyboard shortcut helper class.
 */
public final class Shortcut {
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
        return StringUtils.format("{0}{1}{2}{3}", ctrl ? "ctrl+" : "", alt ? "alt+" : "", shift ? "shift+" : "",
            Input.Keys.toString(key));
    }
}
