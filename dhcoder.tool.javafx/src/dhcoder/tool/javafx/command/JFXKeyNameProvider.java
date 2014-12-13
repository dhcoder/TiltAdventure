package dhcoder.tool.javafx.command;

import dhcoder.tool.collection.BiMap;
import dhcoder.tool.command.KeyNameProvider;
import javafx.scene.input.KeyCode;

import static dhcoder.support.text.StringUtils.format;

public final class JFXKeyNameProvider implements KeyNameProvider {

    BiMap<KeyCode, String> keyNames = new BiMap<KeyCode, String>();

    public JFXKeyNameProvider() {

        keyNames.put(KeyCode.DIGIT0, "0");
        keyNames.put(KeyCode.DIGIT1, "1");
        keyNames.put(KeyCode.DIGIT2, "2");
        keyNames.put(KeyCode.DIGIT3, "3");
        keyNames.put(KeyCode.DIGIT4, "4");
        keyNames.put(KeyCode.DIGIT5, "5");
        keyNames.put(KeyCode.DIGIT6, "6");
        keyNames.put(KeyCode.DIGIT7, "7");
        keyNames.put(KeyCode.DIGIT8, "8");
        keyNames.put(KeyCode.DIGIT9, "9");

        keyNames.put(KeyCode.NUMPAD0, "Numpad0");
        keyNames.put(KeyCode.NUMPAD1, "Numpad1");
        keyNames.put(KeyCode.NUMPAD2, "Numpad2");
        keyNames.put(KeyCode.NUMPAD3, "Numpad3");
        keyNames.put(KeyCode.NUMPAD4, "Numpad4");
        keyNames.put(KeyCode.NUMPAD5, "Numpad5");
        keyNames.put(KeyCode.NUMPAD6, "Numpad6");
        keyNames.put(KeyCode.NUMPAD7, "Numpad7");
        keyNames.put(KeyCode.NUMPAD8, "Numpad8");
        keyNames.put(KeyCode.NUMPAD9, "Numpad9");

        keyNames.put(KeyCode.A, "A");
        keyNames.put(KeyCode.B, "B");
        keyNames.put(KeyCode.C, "C");
        keyNames.put(KeyCode.D, "D");
        keyNames.put(KeyCode.E, "E");
        keyNames.put(KeyCode.F, "F");
        keyNames.put(KeyCode.G, "G");
        keyNames.put(KeyCode.H, "H");
        keyNames.put(KeyCode.I, "I");
        keyNames.put(KeyCode.J, "J");
        keyNames.put(KeyCode.K, "K");
        keyNames.put(KeyCode.L, "L");
        keyNames.put(KeyCode.M, "M");
        keyNames.put(KeyCode.N, "N");
        keyNames.put(KeyCode.O, "O");
        keyNames.put(KeyCode.P, "P");
        keyNames.put(KeyCode.Q, "Q");
        keyNames.put(KeyCode.R, "R");
        keyNames.put(KeyCode.S, "S");
        keyNames.put(KeyCode.T, "T");
        keyNames.put(KeyCode.U, "U");
        keyNames.put(KeyCode.V, "V");
        keyNames.put(KeyCode.W, "W");
        keyNames.put(KeyCode.X, "X");
        keyNames.put(KeyCode.Y, "Y");
        keyNames.put(KeyCode.Z, "Z");

        keyNames.put(KeyCode.COMMA, ",");
        keyNames.put(KeyCode.PERIOD, ".");
        keyNames.put(KeyCode.SLASH, "/");
        keyNames.put(KeyCode.SEMICOLON, ";");
        keyNames.put(KeyCode.EQUALS, "=");
        keyNames.put(KeyCode.OPEN_BRACKET, "[");
        keyNames.put(KeyCode.BACK_SLASH, "\\");
        keyNames.put(KeyCode.CLOSE_BRACKET, "]");

        keyNames.put(KeyCode.ENTER, "Enter");
        keyNames.put(KeyCode.BACK_SPACE, "Backspace");
        keyNames.put(KeyCode.TAB, "Tab");
        keyNames.put(KeyCode.CANCEL, "Cancel");
        keyNames.put(KeyCode.CLEAR, "Clear");
        keyNames.put(KeyCode.SHIFT, "Shift");
        keyNames.put(KeyCode.CONTROL, "Ctrl");
        keyNames.put(KeyCode.ALT, "Alt");
        keyNames.put(KeyCode.PAUSE, "Pause");
        keyNames.put(KeyCode.CAPS, "CapsLock");
        keyNames.put(KeyCode.ESCAPE, "Escape");
        keyNames.put(KeyCode.SPACE, "Space");
        keyNames.put(KeyCode.PAGE_UP, "PageUp");
        keyNames.put(KeyCode.PAGE_DOWN, "PageDown");
        keyNames.put(KeyCode.END, "End");
        keyNames.put(KeyCode.HOME, "Home");
        keyNames.put(KeyCode.LEFT, "Left");
        keyNames.put(KeyCode.UP, "Up");
        keyNames.put(KeyCode.RIGHT, "Right");
        keyNames.put(KeyCode.DOWN, "Down");

        keyNames.put(KeyCode.MULTIPLY, "Multiply");
        keyNames.put(KeyCode.ADD, "Add");
        keyNames.put(KeyCode.SUBTRACT, "Subtract");
        keyNames.put(KeyCode.DECIMAL, "Decimal");
        keyNames.put(KeyCode.DIVIDE, "Divide");
        keyNames.put(KeyCode.DELETE, "Delete");
        keyNames.put(KeyCode.NUM_LOCK, "NumLock");
        keyNames.put(KeyCode.SCROLL_LOCK, "ScrollLock");

        keyNames.put(KeyCode.F1, "F1");
        keyNames.put(KeyCode.F2, "F2");
        keyNames.put(KeyCode.F3, "F3");
        keyNames.put(KeyCode.F4, "F4");
        keyNames.put(KeyCode.F5, "F5");
        keyNames.put(KeyCode.F6, "F6");
        keyNames.put(KeyCode.F7, "F7");
        keyNames.put(KeyCode.F8, "F8");
        keyNames.put(KeyCode.F9, "F9");
        keyNames.put(KeyCode.F10, "F10");
        keyNames.put(KeyCode.F11, "F11");
        keyNames.put(KeyCode.F12, "F12");
        keyNames.put(KeyCode.F13, "F13");
        keyNames.put(KeyCode.F14, "F14");
        keyNames.put(KeyCode.F15, "F15");
        keyNames.put(KeyCode.F16, "F16");
        keyNames.put(KeyCode.F17, "F17");
        keyNames.put(KeyCode.F18, "F18");
        keyNames.put(KeyCode.F19, "F19");
        keyNames.put(KeyCode.F20, "F20");
        keyNames.put(KeyCode.F21, "F21");
        keyNames.put(KeyCode.F22, "F22");
        keyNames.put(KeyCode.F23, "F23");
        keyNames.put(KeyCode.F24, "F24");

        keyNames.put(KeyCode.PRINTSCREEN, "PrintScreen");
        keyNames.put(KeyCode.INSERT, "Insert");
        keyNames.put(KeyCode.HELP, "Help");
        keyNames.put(KeyCode.META, "Meta");
        keyNames.put(KeyCode.BACK_QUOTE, "Back_quote");
        keyNames.put(KeyCode.QUOTE, "'");

        keyNames.put(KeyCode.KP_UP, "NumericUp");
        keyNames.put(KeyCode.KP_DOWN, "NumericDown");
        keyNames.put(KeyCode.KP_LEFT, "NumericLeft");
        keyNames.put(KeyCode.KP_RIGHT, "NumberRight");

        keyNames.put(KeyCode.DEAD_GRAVE, "`");
        keyNames.put(KeyCode.DEAD_ACUTE, "Dead_acute");
        keyNames.put(KeyCode.DEAD_CIRCUMFLEX, "Dead_circumflex");
        keyNames.put(KeyCode.DEAD_TILDE, "~");
        keyNames.put(KeyCode.DEAD_MACRON, "Dead_macron");
        keyNames.put(KeyCode.DEAD_BREVE, "Dead_breve");
        keyNames.put(KeyCode.DEAD_ABOVEDOT, "Dead_abovedot");
        keyNames.put(KeyCode.DEAD_DIAERESIS, "Dead_diaeresis");
        keyNames.put(KeyCode.DEAD_ABOVERING, "Dead_abovering");
        keyNames.put(KeyCode.DEAD_DOUBLEACUTE, "Dead_doubleacute");
        keyNames.put(KeyCode.DEAD_CARON, "Dead_caron");
        keyNames.put(KeyCode.DEAD_CEDILLA, "Dead_cedilla");
        keyNames.put(KeyCode.DEAD_OGONEK, "Dead_ogonek");
        keyNames.put(KeyCode.DEAD_IOTA, "Dead_iota");
        keyNames.put(KeyCode.DEAD_VOICED_SOUND, "Dead_voiced_sound");
        keyNames.put(KeyCode.DEAD_SEMIVOICED_SOUND, "Dead_semivoiced_sound");

        keyNames.put(KeyCode.AMPERSAND, "&");
        keyNames.put(KeyCode.ASTERISK, "*");
        keyNames.put(KeyCode.QUOTEDBL, "\"");
        keyNames.put(KeyCode.LESS, "<");
        keyNames.put(KeyCode.GREATER, ">");
        keyNames.put(KeyCode.BRACELEFT, "{");
        keyNames.put(KeyCode.BRACERIGHT, "}");
        keyNames.put(KeyCode.AT, "@");
        keyNames.put(KeyCode.COLON, ":");
        keyNames.put(KeyCode.CIRCUMFLEX, "^");
        keyNames.put(KeyCode.DOLLAR, "$");
        keyNames.put(KeyCode.EXCLAMATION_MARK, "!");
        keyNames.put(KeyCode.LEFT_PARENTHESIS, "(");
        keyNames.put(KeyCode.NUMBER_SIGN, "#");
        keyNames.put(KeyCode.MINUS, "-");
        keyNames.put(KeyCode.PLUS, "+");
        keyNames.put(KeyCode.RIGHT_PARENTHESIS, ")");
        keyNames.put(KeyCode.UNDERSCORE, "_");
    }

    @Override
    public String getName(final int key) {
        KeyCode keyCode = KeyCode.values()[key];
        if (!keyNames.containsKey(keyCode)) {
            throw new IllegalArgumentException(format("Unknown key 0x{0}", Integer.toString(key, 16)));
        }
        return keyNames.getValue(keyCode);
    }

    @Override
    public int getKey(final String name) throws IllegalArgumentException {
        if (!keyNames.containsValue(name)) {
            throw new IllegalArgumentException(format("Unknown key name {0}", name));
        }
        return keyNames.getKey(name).ordinal();
    }

    public KeyCode getKeyCode(final String name) {
        return getKeyCode(getKey(name));
    }

    public KeyCode getKeyCode(final int key) {
        return KeyCode.values()[key];
    }
}
