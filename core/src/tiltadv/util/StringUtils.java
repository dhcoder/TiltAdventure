package tiltadv.util;

import java.util.ArrayList;
import java.util.List;

public class StringUtils {

    /**
     * Format a string using C# style formatting, i.e. using {0} instead of %0$s.
     * <p/>
     * See <a href="http://msdn.microsoft.com/en-us/library/system.string.format.aspx#Format_Brief">Microsoft's
     * String.Format documentation</a> for more information. Note that this method doesn't support numeric formatting,
     * such as {0.2f}.
     *
     * @param input The formatting string.
     * @param args  Various args whose string values will be used in the final string.
     */
    public static String format(String input, Object... args) {
        StringBuilder builder = new StringBuilder();

        List<String> argStrings = new ArrayList<String>(args.length);
        for (Object arg : args) {
            argStrings.add(arg.toString());
        }

        final int STATE_CONSUME_TEXT = 0;
        final int STATE_GOT_LEFT_BRACE = 1;
        final int STATE_GOT_RIGHT_BRACE = 2;
        final int STATE_PARSING_INDEX = 3;

        int state = STATE_CONSUME_TEXT;
        int formatIndex = 0;

        for (int i = 0; i < input.length(); ++i) {
            char c = input.charAt(i);
            switch (state) {
                case STATE_CONSUME_TEXT:
                    if (c == '{') {
                        state = STATE_GOT_LEFT_BRACE;
                    } else if (c == '}') {
                        state = STATE_GOT_RIGHT_BRACE;
                    } else {
                        builder.append(c);
                    }
                    break;
                case STATE_GOT_LEFT_BRACE:
                    if (c == '{') {
                        state = STATE_CONSUME_TEXT;
                        builder.append('{');
                    } else if (Character.isDigit(c)) {
                        state = STATE_PARSING_INDEX;
                        formatIndex = Character.digit(c, 10);
                    } else {
                        throwUnexpectedCharException(input, c);
                    }
                    break;
                case STATE_PARSING_INDEX:
                    if (Character.isDigit(c)) {
                        formatIndex *= 10;
                        formatIndex += Character.digit(c, 10);
                    } else if (c == '}') {
                        if (formatIndex >= argStrings.size()) {
                            format("Format index {0} out of bounds ({1} arg(s))", formatIndex, argStrings.size());
                        } else {
                            state = STATE_CONSUME_TEXT;
                            builder.append(argStrings.get(formatIndex));
                        }
                    } else {
                        throwUnexpectedCharException(input, c);
                    }
                    break;
                case STATE_GOT_RIGHT_BRACE:
                    if (c == '}') {
                        state = STATE_CONSUME_TEXT;
                        builder.append('}');
                    } else {
                        throwUnexpectedCharException(input, c);
                    }
                    break;
                default:
                    assert false; // Unhandled state, should be impossible to get here.
            }
        }

        if (state != STATE_CONSUME_TEXT) {
            throw new IllegalArgumentException(format("Unexpected end of format string \"{0}\"", input));
        }

        return builder.toString();
    }

    private static void throwUnexpectedCharException(final String input, final char c) {
        throw new IllegalArgumentException(
            format("Unexpected char '{0}' parsing string \"{1}\"", c, input));
    }
}
