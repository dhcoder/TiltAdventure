package d9n.utils;

import java.util.ArrayList;
import java.util.List;

public final class StringUtils {

    private enum FormatState {
        CONSUME_TEXT, // Directly consume regular characters from input string
        GOT_LEFT_BRACE, // Got a left brace, start looking for format indices
        GOT_RIGHT_BRACE, // Got a right brace, see if we're closing off a format index
        PARSING_INDEX, // We're in between braces, parsing a format index
    }

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
    public static String format(final String input, final Object... args) {
        StringBuilder builder = new StringBuilder();

        List<String> argStrings = new ArrayList<String>(args.length);
        for (Object arg : args) {
            argStrings.add(arg.toString());
        }

        FormatState state = FormatState.CONSUME_TEXT;
        int formatIndex = 0;

        for (int i = 0; i < input.length(); ++i) {
            char c = input.charAt(i);
            switch (state) {
                case CONSUME_TEXT:
                    if (c == '{') {
                        state = FormatState.GOT_LEFT_BRACE;
                    } else if (c == '}') {
                        state = FormatState.GOT_RIGHT_BRACE;
                    } else {
                        builder.append(c);
                    }
                    break;
                case GOT_LEFT_BRACE:
                    if (c == '{') {
                        builder.append('{'); // Two left braces -> '{'
                        state = FormatState.CONSUME_TEXT;
                    } else if (Character.isDigit(c)) {
                        state = FormatState.PARSING_INDEX;
                        formatIndex = Character.digit(c, 10);
                    } else {
                        throwUnexpectedCharException(input, c);
                    }
                    break;
                case PARSING_INDEX:
                    if (Character.isDigit(c)) {
                        formatIndex *= 10;
                        formatIndex += Character.digit(c, 10);
                    } else if (c == '}') {
                        if (formatIndex >= argStrings.size()) {
                            format("Format index {0} out of bounds ({1} arg(s)) in string {2}", formatIndex,
                                argStrings.size(), input);
                        } else {
                            state = FormatState.CONSUME_TEXT;
                            builder.append(argStrings.get(formatIndex));
                        }
                    } else {
                        throwUnexpectedCharException(input, c);
                    }
                    break;
                case GOT_RIGHT_BRACE:
                    if (c == '}') {
                        builder.append('}'); // Two right braces -> '}'
                        state = FormatState.CONSUME_TEXT;
                    } else {
                        throwUnexpectedCharException(input, c);
                    }
                    break;
                default:
                    assert false; // Unhandled state, should be impossible to get here.
            }
        }

        if (state != FormatState.CONSUME_TEXT) {
            throw new IllegalArgumentException(format("Unexpected end of format string \"{0}\"", input));
        }

        return builder.toString();
    }

    private static void throwUnexpectedCharException(final String input, final char c) {
        throw new IllegalArgumentException(format("Unexpected char '{0}' parsing string \"{1}\"", c, input));
    }
}
