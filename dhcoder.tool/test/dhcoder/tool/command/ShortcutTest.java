package dhcoder.tool.command;

import dhcoder.tool.collection.BiMap;
import org.junit.Before;
import org.junit.Test;

import java.text.ParseException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

public final class ShortcutTest {

    private enum Key {
        A,
        NUM_1,
        GRAVE,
        NUMPAD_9,
    }

    private static final class TestKeyNameProvider implements KeyNameProvider {
        BiMap<Key, String> keyNames = new BiMap<Key, String>();

        public TestKeyNameProvider() {
            keyNames.put(Key.A, "A");
            keyNames.put(Key.NUM_1, "1");
            keyNames.put(Key.GRAVE, "`");
            keyNames.put(Key.NUMPAD_9, "Numpad9");
        }

        @Override
        public String getName(final int key) {
            return keyNames.getValue(Key.values()[key]);
        }

        @Override
        public int getKey(final String name) throws IllegalArgumentException {
            return keyNames.getKey(name).ordinal();
        }
    }

    @Before
    public void setUp() {
        Shortcut.setKeyNameProvider(new TestKeyNameProvider());
    }

    @Test
    public void testParseShortcuts() throws ParseException {
        Shortcut shortcutA = Shortcut.fromString("A");
        assertThat(shortcutA.ctrl(), equalTo(false));
        assertThat(shortcutA.alt(), equalTo(false));
        assertThat(shortcutA.shift(), equalTo(false));
        assertThat(shortcutA.key(), equalTo(Key.A.ordinal()));

        Shortcut shortcutCtrlExcl = Shortcut.fromString("Ctrl+1");
        assertThat(shortcutCtrlExcl.ctrl(), equalTo(true));
        assertThat(shortcutCtrlExcl.alt(), equalTo(false));
        assertThat(shortcutCtrlExcl.shift(), equalTo(false));
        assertThat(shortcutCtrlExcl.key(), equalTo(Key.NUM_1.ordinal()));

        Shortcut shortcutAltShiftGrave = Shortcut.fromString("Alt+Shift+`");
        assertThat(shortcutAltShiftGrave.ctrl(), equalTo(false));
        assertThat(shortcutAltShiftGrave.alt(), equalTo(true));
        assertThat(shortcutAltShiftGrave.shift(), equalTo(true));
        assertThat(shortcutAltShiftGrave.key(), equalTo(Key.GRAVE.ordinal()));

        Shortcut shortcutCtrlAltShiftPlus = Shortcut.fromString("Ctrl+Alt+Shift+Numpad9");
        assertThat(shortcutCtrlAltShiftPlus.ctrl(), equalTo(true));
        assertThat(shortcutCtrlAltShiftPlus.alt(), equalTo(true));
        assertThat(shortcutCtrlAltShiftPlus.shift(), equalTo(true));
        assertThat(shortcutCtrlAltShiftPlus.key(), equalTo(Key.NUMPAD_9.ordinal()));
    }
}