package dhcoder.tool.swing.command;

import dhcoder.tool.collection.BiMap;
import dhcoder.tool.command.KeyNameProvider;

import java.awt.event.KeyEvent;

import static dhcoder.support.text.StringUtils.format;

public final class SwingKeyNameProvider implements KeyNameProvider {

    BiMap<Integer, String> keyNames = new BiMap<Integer, String>();

    public SwingKeyNameProvider() {
        for (int keyCode = KeyEvent.VK_0; keyCode <= KeyEvent.VK_9; keyCode++) {
            keyNames.put(keyCode, String.valueOf((char)keyCode));
        }
        for (int keyCode = KeyEvent.VK_A; keyCode <= KeyEvent.VK_Z; keyCode++) {
            keyNames.put(keyCode, String.valueOf((char)keyCode));
        }
        for (int keyCode = KeyEvent.VK_NUMPAD0; keyCode <= KeyEvent.VK_NUMPAD9; keyCode++) {
            char c = (char)(keyCode - KeyEvent.VK_NUMPAD0 + '0');
            keyNames.put(keyCode, "Numpad" + c);
        }

        keyNames.put(KeyEvent.VK_COMMA, ",");
        keyNames.put(KeyEvent.VK_PERIOD, ".");
        keyNames.put(KeyEvent.VK_SLASH, "/");
        keyNames.put(KeyEvent.VK_SEMICOLON, ";");
        keyNames.put(KeyEvent.VK_EQUALS, "=");
        keyNames.put(KeyEvent.VK_OPEN_BRACKET, "[");
        keyNames.put(KeyEvent.VK_BACK_SLASH, "\\");
        keyNames.put(KeyEvent.VK_CLOSE_BRACKET, "]");

        keyNames.put(KeyEvent.VK_ENTER, "Enter");
        keyNames.put(KeyEvent.VK_BACK_SPACE, "Backspace");
        keyNames.put(KeyEvent.VK_TAB, "Tab");
        keyNames.put(KeyEvent.VK_CANCEL, "Cancel");
        keyNames.put(KeyEvent.VK_CLEAR, "Clear");
        keyNames.put(KeyEvent.VK_SHIFT, "Shift");
        keyNames.put(KeyEvent.VK_CONTROL, "Ctrl");
        keyNames.put(KeyEvent.VK_ALT, "Alt");
        keyNames.put(KeyEvent.VK_PAUSE, "Pause");
        keyNames.put(KeyEvent.VK_CAPS_LOCK, "CapsLock");
        keyNames.put(KeyEvent.VK_ESCAPE, "Escape");
        keyNames.put(KeyEvent.VK_SPACE, "Space");
        keyNames.put(KeyEvent.VK_PAGE_UP, "PageUp");
        keyNames.put(KeyEvent.VK_PAGE_DOWN, "PageDown");
        keyNames.put(KeyEvent.VK_END, "End");
        keyNames.put(KeyEvent.VK_HOME, "Home");
        keyNames.put(KeyEvent.VK_LEFT, "Left");
        keyNames.put(KeyEvent.VK_UP, "Up");
        keyNames.put(KeyEvent.VK_RIGHT, "Right");
        keyNames.put(KeyEvent.VK_DOWN, "Down");

        keyNames.put(KeyEvent.VK_MULTIPLY, "Multiply");
        keyNames.put(KeyEvent.VK_ADD, "Add");
        keyNames.put(KeyEvent.VK_SUBTRACT, "Subtract");
        keyNames.put(KeyEvent.VK_DECIMAL, "Decimal");
        keyNames.put(KeyEvent.VK_DIVIDE, "Divide");
        keyNames.put(KeyEvent.VK_DELETE, "Delete");
        keyNames.put(KeyEvent.VK_NUM_LOCK, "NumLock");
        keyNames.put(KeyEvent.VK_SCROLL_LOCK, "ScrollLock");

        keyNames.put(KeyEvent.VK_F1, "F1");
        keyNames.put(KeyEvent.VK_F2, "F2");
        keyNames.put(KeyEvent.VK_F3, "F3");
        keyNames.put(KeyEvent.VK_F4, "F4");
        keyNames.put(KeyEvent.VK_F5, "F5");
        keyNames.put(KeyEvent.VK_F6, "F6");
        keyNames.put(KeyEvent.VK_F7, "F7");
        keyNames.put(KeyEvent.VK_F8, "F8");
        keyNames.put(KeyEvent.VK_F9, "F9");
        keyNames.put(KeyEvent.VK_F10, "F10");
        keyNames.put(KeyEvent.VK_F11, "F11");
        keyNames.put(KeyEvent.VK_F12, "F12");
        keyNames.put(KeyEvent.VK_F13, "F13");
        keyNames.put(KeyEvent.VK_F14, "F14");
        keyNames.put(KeyEvent.VK_F15, "F15");
        keyNames.put(KeyEvent.VK_F16, "F16");
        keyNames.put(KeyEvent.VK_F17, "F17");
        keyNames.put(KeyEvent.VK_F18, "F18");
        keyNames.put(KeyEvent.VK_F19, "F19");
        keyNames.put(KeyEvent.VK_F20, "F20");
        keyNames.put(KeyEvent.VK_F21, "F21");
        keyNames.put(KeyEvent.VK_F22, "F22");
        keyNames.put(KeyEvent.VK_F23, "F23");
        keyNames.put(KeyEvent.VK_F24, "F24");

        keyNames.put(KeyEvent.VK_PRINTSCREEN, "PrintScreen");
        keyNames.put(KeyEvent.VK_INSERT, "Insert");
        keyNames.put(KeyEvent.VK_HELP, "Help");
        keyNames.put(KeyEvent.VK_META, "Meta");
        keyNames.put(KeyEvent.VK_BACK_QUOTE, "Back_quote");
        keyNames.put(KeyEvent.VK_QUOTE, "'");

        keyNames.put(KeyEvent.VK_KP_UP, "NumericUp");
        keyNames.put(KeyEvent.VK_KP_DOWN, "NumericDown");
        keyNames.put(KeyEvent.VK_KP_LEFT, "NumericLeft");
        keyNames.put(KeyEvent.VK_KP_RIGHT, "NumberRight");

        keyNames.put(KeyEvent.VK_DEAD_GRAVE, "`");
        keyNames.put(KeyEvent.VK_DEAD_ACUTE, "Dead_acute");
        keyNames.put(KeyEvent.VK_DEAD_CIRCUMFLEX, "Dead_circumflex");
        keyNames.put(KeyEvent.VK_DEAD_TILDE, "~");
        keyNames.put(KeyEvent.VK_DEAD_MACRON, "Dead_macron");
        keyNames.put(KeyEvent.VK_DEAD_BREVE, "Dead_breve");
        keyNames.put(KeyEvent.VK_DEAD_ABOVEDOT, "Dead_abovedot");
        keyNames.put(KeyEvent.VK_DEAD_DIAERESIS, "Dead_diaeresis");
        keyNames.put(KeyEvent.VK_DEAD_ABOVERING, "Dead_abovering");
        keyNames.put(KeyEvent.VK_DEAD_DOUBLEACUTE, "Dead_doubleacute");
        keyNames.put(KeyEvent.VK_DEAD_CARON, "Dead_caron");
        keyNames.put(KeyEvent.VK_DEAD_CEDILLA, "Dead_cedilla");
        keyNames.put(KeyEvent.VK_DEAD_OGONEK, "Dead_ogonek");
        keyNames.put(KeyEvent.VK_DEAD_IOTA, "Dead_iota");
        keyNames.put(KeyEvent.VK_DEAD_VOICED_SOUND, "Dead_voiced_sound");
        keyNames.put(KeyEvent.VK_DEAD_SEMIVOICED_SOUND, "Dead_semivoiced_sound");

        keyNames.put(KeyEvent.VK_AMPERSAND, "&");
        keyNames.put(KeyEvent.VK_ASTERISK, "*");
        keyNames.put(KeyEvent.VK_QUOTEDBL, "\"");
        keyNames.put(KeyEvent.VK_LESS, "<");
        keyNames.put(KeyEvent.VK_GREATER, ">");
        keyNames.put(KeyEvent.VK_BRACELEFT, "{");
        keyNames.put(KeyEvent.VK_BRACERIGHT, "}");
        keyNames.put(KeyEvent.VK_AT, "@");
        keyNames.put(KeyEvent.VK_COLON, ":");
        keyNames.put(KeyEvent.VK_CIRCUMFLEX, "^");
        keyNames.put(KeyEvent.VK_DOLLAR, "$");
        keyNames.put(KeyEvent.VK_EXCLAMATION_MARK, "!");
        keyNames.put(KeyEvent.VK_LEFT_PARENTHESIS, "(");
        keyNames.put(KeyEvent.VK_NUMBER_SIGN, "#");
        keyNames.put(KeyEvent.VK_MINUS, "-");
        keyNames.put(KeyEvent.VK_PLUS, "+");
        keyNames.put(KeyEvent.VK_RIGHT_PARENTHESIS, ")");
        keyNames.put(KeyEvent.VK_UNDERSCORE, "_");
    }

    @Override
    public String getName(final int key) {
        if (!keyNames.containsKey(key)) {
            throw new IllegalArgumentException(format("Unknown key 0x{0}", Integer.toString(key, 16)));
        }
        return keyNames.getValue(key);
    }

    @Override
    public int getKey(final String name) throws IllegalArgumentException {
        if (!keyNames.containsValue(name)) {
            throw new IllegalArgumentException(format("Unknown key name {0}", name));
        }
        return keyNames.getKey(name);
    }
}
