package dhcoder.tool.javafx.command;

import javafx.scene.input.KeyCode;

/**
 * Using syntax like {@link KeyCode}.SLASH.ordinal() everywhere you need to cast KeyCode -> int is just too verbose.
 * This class provides direct integer values for the corresponding {@link KeyCode}, so, for the previous example, you
 * would just use {@link JFXKey}.SLASH instead.
 */
public final class JFXKey {

   public static final int DIGIT0 = KeyCode.DIGIT0.ordinal();
   public static final int DIGIT1 = KeyCode.DIGIT1.ordinal();
   public static final int DIGIT2 = KeyCode.DIGIT2.ordinal();
   public static final int DIGIT3 = KeyCode.DIGIT3.ordinal();
   public static final int DIGIT4 = KeyCode.DIGIT4.ordinal();
   public static final int DIGIT5 = KeyCode.DIGIT5.ordinal();
   public static final int DIGIT6 = KeyCode.DIGIT6.ordinal();
   public static final int DIGIT7 = KeyCode.DIGIT7.ordinal();
   public static final int DIGIT8 = KeyCode.DIGIT8.ordinal();
   public static final int DIGIT9 = KeyCode.DIGIT9.ordinal();

   public static final int NUMPAD0 = KeyCode.NUMPAD0.ordinal();
   public static final int NUMPAD1 = KeyCode.NUMPAD1.ordinal();
   public static final int NUMPAD2 = KeyCode.NUMPAD2.ordinal();
   public static final int NUMPAD3 = KeyCode.NUMPAD3.ordinal();
   public static final int NUMPAD4 = KeyCode.NUMPAD4.ordinal();
   public static final int NUMPAD5 = KeyCode.NUMPAD5.ordinal();
   public static final int NUMPAD6 = KeyCode.NUMPAD6.ordinal();
   public static final int NUMPAD7 = KeyCode.NUMPAD7.ordinal();
   public static final int NUMPAD8 = KeyCode.NUMPAD8.ordinal();
   public static final int NUMPAD9 = KeyCode.NUMPAD9.ordinal();

   public static final int A = KeyCode.A.ordinal();
   public static final int B = KeyCode.B.ordinal();
   public static final int C = KeyCode.C.ordinal();
   public static final int D = KeyCode.D.ordinal();
   public static final int E = KeyCode.E.ordinal();
   public static final int F = KeyCode.F.ordinal();
   public static final int G = KeyCode.G.ordinal();
   public static final int H = KeyCode.H.ordinal();
   public static final int I = KeyCode.I.ordinal();
   public static final int J = KeyCode.J.ordinal();
   public static final int K = KeyCode.K.ordinal();
   public static final int L = KeyCode.L.ordinal();
   public static final int M = KeyCode.M.ordinal();
   public static final int N = KeyCode.N.ordinal();
   public static final int O = KeyCode.O.ordinal();
   public static final int P = KeyCode.P.ordinal();
   public static final int Q = KeyCode.Q.ordinal();
   public static final int R = KeyCode.R.ordinal();
   public static final int S = KeyCode.S.ordinal();
   public static final int T = KeyCode.T.ordinal();
   public static final int U = KeyCode.U.ordinal();
   public static final int V = KeyCode.V.ordinal();
   public static final int W = KeyCode.W.ordinal();
   public static final int X = KeyCode.X.ordinal();
   public static final int Y = KeyCode.Y.ordinal();
   public static final int Z = KeyCode.Z.ordinal();

   public static final int COMMA = KeyCode.COMMA.ordinal();
   public static final int PERIOD = KeyCode.PERIOD.ordinal();
   public static final int SLASH = KeyCode.SLASH.ordinal();
   public static final int SEMICOLON = KeyCode.SEMICOLON.ordinal();
   public static final int EQUALS = KeyCode.EQUALS.ordinal();
   public static final int OPEN_BRACKET = KeyCode.OPEN_BRACKET.ordinal();
   public static final int BACK_SLASH = KeyCode.BACK_SLASH.ordinal();
   public static final int CLOSE_BRACKET = KeyCode.CLOSE_BRACKET.ordinal();

   public static final int ENTER = KeyCode.ENTER.ordinal();
   public static final int BACK_SPACE = KeyCode.BACK_SPACE.ordinal();
   public static final int TAB = KeyCode.TAB.ordinal();
   public static final int CANCEL = KeyCode.CANCEL.ordinal();
   public static final int CLEAR = KeyCode.CLEAR.ordinal();
   public static final int SHIFT = KeyCode.SHIFT.ordinal();
   public static final int CONTROL = KeyCode.CONTROL.ordinal();
   public static final int ALT = KeyCode.ALT.ordinal();
   public static final int PAUSE = KeyCode.PAUSE.ordinal();
   public static final int CAPS = KeyCode.CAPS.ordinal();
   public static final int ESCAPE = KeyCode.ESCAPE.ordinal();
   public static final int SPACE = KeyCode.SPACE.ordinal();
   public static final int PAGE_UP = KeyCode.PAGE_UP.ordinal();
   public static final int PAGE_DOWN = KeyCode.PAGE_DOWN.ordinal();
   public static final int END = KeyCode.END.ordinal();
   public static final int HOME = KeyCode.HOME.ordinal();
   public static final int LEFT = KeyCode.LEFT.ordinal();
   public static final int UP = KeyCode.UP.ordinal();
   public static final int RIGHT = KeyCode.RIGHT.ordinal();
   public static final int DOWN = KeyCode.DOWN.ordinal();

   public static final int MULTIPLY = KeyCode.MULTIPLY.ordinal();
   public static final int ADD = KeyCode.ADD.ordinal();
   public static final int SUBTRACT = KeyCode.SUBTRACT.ordinal();
   public static final int DECIMAL = KeyCode.DECIMAL.ordinal();
   public static final int DIVIDE = KeyCode.DIVIDE.ordinal();
   public static final int DELETE = KeyCode.DELETE.ordinal();
   public static final int NUM_LOCK = KeyCode.NUM_LOCK.ordinal();
   public static final int SCROLL_LOCK = KeyCode.SCROLL_LOCK.ordinal();

   public static final int F1 = KeyCode.F1.ordinal();
   public static final int F2 = KeyCode.F2.ordinal();
   public static final int F3 = KeyCode.F3.ordinal();
   public static final int F4 = KeyCode.F4.ordinal();
   public static final int F5 = KeyCode.F5.ordinal();
   public static final int F6 = KeyCode.F6.ordinal();
   public static final int F7 = KeyCode.F7.ordinal();
   public static final int F8 = KeyCode.F8.ordinal();
   public static final int F9 = KeyCode.F9.ordinal();
   public static final int F10 = KeyCode.F10.ordinal();
   public static final int F11 = KeyCode.F11.ordinal();
   public static final int F12 = KeyCode.F12.ordinal();
   public static final int F13 = KeyCode.F13.ordinal();
   public static final int F14 = KeyCode.F14.ordinal();
   public static final int F15 = KeyCode.F15.ordinal();
   public static final int F16 = KeyCode.F16.ordinal();
   public static final int F17 = KeyCode.F17.ordinal();
   public static final int F18 = KeyCode.F18.ordinal();
   public static final int F19 = KeyCode.F19.ordinal();
   public static final int F20 = KeyCode.F20.ordinal();
   public static final int F21 = KeyCode.F21.ordinal();
   public static final int F22 = KeyCode.F22.ordinal();
   public static final int F23 = KeyCode.F23.ordinal();
   public static final int F24 = KeyCode.F24.ordinal();

   public static final int PRINTSCREEN = KeyCode.PRINTSCREEN.ordinal();
   public static final int INSERT = KeyCode.INSERT.ordinal();
   public static final int HELP = KeyCode.HELP.ordinal();
   public static final int META = KeyCode.META.ordinal();
   public static final int BACK_QUOTE = KeyCode.BACK_QUOTE.ordinal();
   public static final int QUOTE = KeyCode.QUOTE.ordinal();

   public static final int KP_UP = KeyCode.KP_UP.ordinal();
   public static final int KP_DOWN = KeyCode.KP_DOWN.ordinal();
   public static final int KP_LEFT = KeyCode.KP_LEFT.ordinal();
   public static final int KP_RIGHT = KeyCode.KP_RIGHT.ordinal();

   public static final int DEAD_GRAVE = KeyCode.DEAD_GRAVE.ordinal();
   public static final int DEAD_ACUTE = KeyCode.DEAD_ACUTE.ordinal();
   public static final int DEAD_CIRCUMFLEX = KeyCode.DEAD_CIRCUMFLEX.ordinal();
   public static final int DEAD_TILDE = KeyCode.DEAD_TILDE.ordinal();
   public static final int DEAD_MACRON = KeyCode.DEAD_MACRON.ordinal();
   public static final int DEAD_BREVE = KeyCode.DEAD_BREVE.ordinal();
   public static final int DEAD_ABOVEDOT = KeyCode.DEAD_ABOVEDOT.ordinal();
   public static final int DEAD_DIAERESIS = KeyCode.DEAD_DIAERESIS.ordinal();
   public static final int DEAD_ABOVERING = KeyCode.DEAD_ABOVERING.ordinal();
   public static final int DEAD_DOUBLEACUTE = KeyCode.DEAD_DOUBLEACUTE.ordinal();
   public static final int DEAD_CARON = KeyCode.DEAD_CARON.ordinal();
   public static final int DEAD_CEDILLA = KeyCode.DEAD_CEDILLA.ordinal();
   public static final int DEAD_OGONEK = KeyCode.DEAD_OGONEK.ordinal();
   public static final int DEAD_IOTA = KeyCode.DEAD_IOTA.ordinal();
   public static final int DEAD_VOICED_SOUND = KeyCode.DEAD_VOICED_SOUND.ordinal();
   public static final int DEAD_SEMIVOICED_SOUND = KeyCode.DEAD_SEMIVOICED_SOUND.ordinal();

   public static final int AMPERSAND = KeyCode.AMPERSAND.ordinal();
   public static final int ASTERISK = KeyCode.ASTERISK.ordinal();
   public static final int QUOTEDBL = KeyCode.QUOTEDBL.ordinal();
   public static final int LESS = KeyCode.LESS.ordinal();
   public static final int GREATER = KeyCode.GREATER.ordinal();
   public static final int BRACELEFT = KeyCode.BRACELEFT.ordinal();
   public static final int BRACERIGHT = KeyCode.BRACERIGHT.ordinal();
   public static final int AT = KeyCode.AT.ordinal();
   public static final int COLON = KeyCode.COLON.ordinal();
   public static final int CIRCUMFLEX = KeyCode.CIRCUMFLEX.ordinal();
   public static final int DOLLAR = KeyCode.DOLLAR.ordinal();
   public static final int EXCLAMATION_MARK = KeyCode.EXCLAMATION_MARK.ordinal();
   public static final int LEFT_PARENTHESIS = KeyCode.LEFT_PARENTHESIS.ordinal();
   public static final int NUMBER_SIGN = KeyCode.NUMBER_SIGN.ordinal();
   public static final int MINUS = KeyCode.MINUS.ordinal();
   public static final int PLUS = KeyCode.PLUS.ordinal();
   public static final int RIGHT_PARENTHESIS = KeyCode.RIGHT_PARENTHESIS.ordinal();
   public static final int UNDERSCORE = KeyCode.UNDERSCORE.ordinal();
}
