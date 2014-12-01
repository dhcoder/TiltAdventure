package dhcoder.tool.command;

import com.badlogic.gdx.Input.Keys;
import org.junit.Test;

import java.text.ParseException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

public final class ShortcutTest {
    @Test
    public void testParseShortcuts() throws ParseException {
        Shortcut shortcutA = Shortcut.fromString("A");
        assertThat(shortcutA.ctrl(), equalTo(false));
        assertThat(shortcutA.alt(), equalTo(false));
        assertThat(shortcutA.shift(), equalTo(false));
        assertThat(shortcutA.key(), equalTo(Keys.A));

        Shortcut shortcutCtrlExcl = Shortcut.fromString("Ctrl+1");
        assertThat(shortcutCtrlExcl.ctrl(), equalTo(true));
        assertThat(shortcutCtrlExcl.alt(), equalTo(false));
        assertThat(shortcutCtrlExcl.shift(), equalTo(false));
        assertThat(shortcutCtrlExcl.key(), equalTo(Keys.NUM_1));

        Shortcut shortcutAltShiftGrave = Shortcut.fromString("Alt+Shift+`");
        assertThat(shortcutAltShiftGrave.ctrl(), equalTo(false));
        assertThat(shortcutAltShiftGrave.alt(), equalTo(true));
        assertThat(shortcutAltShiftGrave.shift(), equalTo(true));
        assertThat(shortcutAltShiftGrave.key(), equalTo(Keys.GRAVE));

        Shortcut shortcutCtrlAltShiftPlus = Shortcut.fromString("Ctrl+Alt+Shift+=");
        assertThat(shortcutCtrlAltShiftPlus.ctrl(), equalTo(true));
        assertThat(shortcutCtrlAltShiftPlus.alt(), equalTo(true));
        assertThat(shortcutCtrlAltShiftPlus.shift(), equalTo(true));
        assertThat(shortcutCtrlAltShiftPlus.key(), equalTo(Keys.EQUALS));
    }
}