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

    private final int KEY_A = Key.A.ordinal();
    private final int KEY_NUM_1 = Key.NUM_1.ordinal();
    private final int KEY_GRAVE = Key.GRAVE.ordinal();
    private final int KEY_NUMPAD_9 = Key.NUMPAD_9.ordinal();

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
        assertThat(shortcutA.key(), equalTo(KEY_A));

        Shortcut shortcutCtrlExcl = Shortcut.fromString("Ctrl+1");
        assertThat(shortcutCtrlExcl.ctrl(), equalTo(true));
        assertThat(shortcutCtrlExcl.alt(), equalTo(false));
        assertThat(shortcutCtrlExcl.shift(), equalTo(false));
        assertThat(shortcutCtrlExcl.key(), equalTo(KEY_NUM_1));

        Shortcut shortcutAltShiftGrave = Shortcut.fromString("Alt+Shift+`");
        assertThat(shortcutAltShiftGrave.ctrl(), equalTo(false));
        assertThat(shortcutAltShiftGrave.alt(), equalTo(true));
        assertThat(shortcutAltShiftGrave.shift(), equalTo(true));
        assertThat(shortcutAltShiftGrave.key(), equalTo(KEY_GRAVE));

        Shortcut shortcutCtrlAltShiftPlus = Shortcut.fromString("Ctrl+Alt+Shift+Numpad9");
        assertThat(shortcutCtrlAltShiftPlus.ctrl(), equalTo(true));
        assertThat(shortcutCtrlAltShiftPlus.alt(), equalTo(true));
        assertThat(shortcutCtrlAltShiftPlus.shift(), equalTo(true));
        assertThat(shortcutCtrlAltShiftPlus.key(), equalTo(KEY_NUMPAD_9));
    }

    @Test
    public void testShortcutBuilder() {
        Shortcut shortcutA = Shortcut.of(KEY_A).build();
        assertThat(shortcutA.ctrl(), equalTo(false));
        assertThat(shortcutA.alt(), equalTo(false));
        assertThat(shortcutA.shift(), equalTo(false));
        assertThat(shortcutA.key(), equalTo(KEY_A));

        Shortcut shortcutCtrlExcl = Shortcut.of(KEY_NUM_1).ctrl().build();
        assertThat(shortcutCtrlExcl.ctrl(), equalTo(true));
        assertThat(shortcutCtrlExcl.alt(), equalTo(false));
        assertThat(shortcutCtrlExcl.shift(), equalTo(false));
        assertThat(shortcutCtrlExcl.key(), equalTo(KEY_NUM_1));

        Shortcut shortcutAltShiftGrave = Shortcut.of(KEY_GRAVE).alt().shift().build();
        assertThat(shortcutAltShiftGrave.ctrl(), equalTo(false));
        assertThat(shortcutAltShiftGrave.alt(), equalTo(true));
        assertThat(shortcutAltShiftGrave.shift(), equalTo(true));
        assertThat(shortcutAltShiftGrave.key(), equalTo(KEY_GRAVE));

        Shortcut shortcutCtrlAltShiftPlus = Shortcut.of(KEY_NUMPAD_9).ctrl().alt().shift().build();
        assertThat(shortcutCtrlAltShiftPlus.ctrl(), equalTo(true));
        assertThat(shortcutCtrlAltShiftPlus.alt(), equalTo(true));
        assertThat(shortcutCtrlAltShiftPlus.shift(), equalTo(true));
        assertThat(shortcutCtrlAltShiftPlus.key(), equalTo(KEY_NUMPAD_9));
    }

    @Test
    public void testCmdModifier() {
        boolean oldCmdAsCtrlValue = Shortcut.TREAT_CMD_AS_CTRL;

        Shortcut.TREAT_CMD_AS_CTRL = false;
        Shortcut shortcutWithCmd = Shortcut.of(KEY_A).cmd().build();
        assertThat(shortcutWithCmd.cmd(), equalTo(true));

        Shortcut.TREAT_CMD_AS_CTRL = true;
        Shortcut shortcutWithoutCmd = Shortcut.of(KEY_A).cmd().build();
        assertThat(shortcutWithoutCmd.cmd(), equalTo(false));
        assertThat(shortcutWithoutCmd.ctrl(), equalTo(true));

        Shortcut.TREAT_CMD_AS_CTRL = oldCmdAsCtrlValue;
    }

    @Test
    public void testParseCmdModifier() throws ParseException {
        boolean oldCmdAsCtrlValue = Shortcut.TREAT_CMD_AS_CTRL;

        try {
            Shortcut.TREAT_CMD_AS_CTRL = false;
            Shortcut shortcutWithCmd = Shortcut.fromString("Cmd+A");
            assertThat(shortcutWithCmd.cmd(), equalTo(true));

            Shortcut.TREAT_CMD_AS_CTRL = true;
            Shortcut shortcutWithoutCmd = Shortcut.fromString("Cmd+A");
            assertThat(shortcutWithoutCmd.cmd(), equalTo(false));
            assertThat(shortcutWithoutCmd.ctrl(), equalTo(true));
        }
        finally {
            Shortcut.TREAT_CMD_AS_CTRL = oldCmdAsCtrlValue;
        }
    }
}