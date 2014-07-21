package dhcoder.support.utils;

import dhcoder.support.memory.MutableFloat;
import dhcoder.support.memory.MutableInt;

import java.io.IOException;
import java.nio.BufferOverflowException;
import java.nio.CharBuffer;

public final class StringUtils {

    private enum FormatState {
        CONSUME_TEXT, // Directly consume regular characters from input string
        GOT_LEFT_BRACE, // Got a left brace, start looking for format indices
        GOT_RIGHT_BRACE, // Got a right brace, see if we're closing off a format index
        PARSING_INDEX, // We're in between braces, parsing a format index
    }

    private enum ParameterType {
        STRING,
        INTEGER,
    }

    /**
     * Format a string in a way inspired C# style formatting, i.e. using {0} instead of %0$s.
     * <p/>
     * Additionally, this method supports some custom formatting style, particularly {0i}, {0f#}, {0c}, and {0b} for
     * Integer, Float, Character, and Boolean handling. If the parameter does not match the expect type, an
     * {@link IllegalArgumentException} is thrown.
     * <p/>
     * The number specified after "f" is for the number of digits this formatter should round to (so, for example,
     * {3f2} means the 4th parameter is a float which should be rounded to the 2nd digit). {0f2} with the value 5 will
     * result in "5.00". If no digit is specified after "f", 2 digits is the default.
     * <p/>
     * See <a href="http://msdn.microsoft.com/en-us/library/system.string.format.aspx#Format_Brief">Microsoft's
     * String.Format documentation</a> for more information, keeping in mind our method's custom approach to numeric
     * formatting.
     *
     * @param formatString The formatting string.
     * @param args         Various args whose string values will be used in the final string.
     */
    public static String format(final String formatString, final Object... args) {
        StringBuilder builder = new StringBuilder();
        formatInto(builder, formatString, args);
        return builder.toString();
    }

    /**
     * Like {@link #format(String, Object...)}, but useful if you want to write a formatted string into a pre-allocated
     * char array, such as a {@link CharBuffer}. If you further wants to avoid allocations from auto-boxing,
     * be sure to pre-allocate the proper {@link MutableInt}, {@link MutableFloat}, etc., instead of passing in a
     * primitive type.
     */
    public static void formatInto(final Appendable dest, final String formatString, final Object... args) {
        FormatState state = FormatState.CONSUME_TEXT;
        int formatIndex = 0;
        ParameterType parameterType = ParameterType.STRING;

        try {
            int formatStringLength = formatString.length(); // Avoid allocation from calling toCharArray
            for (int i = 0; i < formatStringLength; ++i) {
                char c = formatString.charAt(i);
                switch (state) {
                    case CONSUME_TEXT:
                        if (c == '{') {
                            state = FormatState.GOT_LEFT_BRACE;
                        }
                        else if (c == '}') {
                            state = FormatState.GOT_RIGHT_BRACE;
                        }
                        else {
                            dest.append(c);
                        }
                        break;
                    case GOT_LEFT_BRACE:
                        if (c == '{') {
                            dest.append('{'); // Two left braces -> '{'
                            state = FormatState.CONSUME_TEXT;
                        }
                        else if (Character.isDigit(c)) {
                            state = FormatState.PARSING_INDEX;
                            formatIndex = Character.digit(c, 10);
                        }
                        else {
                            throwUnexpectedCharException(formatString, c);
                        }
                        break;
                    case PARSING_INDEX:
                        if (Character.isDigit(c)) {
                            formatIndex *= 10;
                            formatIndex += Character.digit(c, 10);
                        }
                        else if (c == 'd') {
                            parameterType = ParameterType.INTEGER;
                        }
                        else if (c == '}') {
                            if (formatIndex >= args.length) {
                                format("Format index {0} out of bounds ({1} arg(s)) in string {2}", formatIndex,
                                    args.length, formatString);
                            }
                            else {
                                state = FormatState.CONSUME_TEXT;
                                appendParameter(dest, parameterType, args[formatIndex]);
                                parameterType = ParameterType.STRING; // Reset parameter type for next time
                            }
                        }
                        else {
                            throwUnexpectedCharException(formatString, c);
                        }
                        break;
                    case GOT_RIGHT_BRACE:
                        if (c == '}') {
                            dest.append('}'); // Two right braces -> '}'
                            state = FormatState.CONSUME_TEXT;
                        }
                        else {
                            throwUnexpectedCharException(formatString, c);
                        }
                        break;
                    default:
                        assert false; // Unhandled state, should be impossible to get here.
                }
            }
        } catch (IOException e) {
            // Not sure how to get this. Appendable interface requires it.
            throw new IllegalArgumentException(
                format("Format string \"{0}\" threw I/O exception.\n{1}", formatString, e));
        } catch (BufferOverflowException e) {
            // format uses a StringBuilder, so no BufferOverflow is possible
            String safeFormat = format(formatString, args);
            throw new IllegalArgumentException(format("Problem writing \"{0}\" into a fixed buffer.", safeFormat));
        }

        if (state != FormatState.CONSUME_TEXT) {
            throw new IllegalArgumentException(format("Unexpected end of format string \"{0}\"", formatString));
        }
    }

    private static void appendParameter(final Appendable dest, final ParameterType parameterType, final Object arg)
        throws IOException {
        if (parameterType == ParameterType.STRING) {
            dest.append(arg.toString());
        }
        else if (parameterType == ParameterType.INTEGER) {
            int value = 0;
            if (arg instanceof Number) {
                value = ((Number)arg).intValue();
            }
            else {
                throw new IllegalArgumentException(format("Expected numeric parameter, got {0}", arg.getClass()));
            }

            appendInteger(dest, value);
        }
    }

    private static void appendInteger(final Appendable dest, final int value) throws IOException {
        int numDigits = 0;
        int valueCopy = value;
        boolean isNegative = false;
        if (valueCopy < 0) {
            isNegative = true;
            valueCopy = -valueCopy;
        }

        int tensAccumulator = 1;

        while (valueCopy / tensAccumulator > 0) {
            numDigits++;
            tensAccumulator *= 10;
        }

        if (isNegative) {
            dest.append('-');
        }

        if (numDigits == 0) {
            dest.append('0');
        }

        for (int i = 0; i < numDigits; ++i) {
            tensAccumulator /= 10;
            int digit = valueCopy / tensAccumulator;
            dest.append(Character.forDigit(valueCopy / tensAccumulator, 10));
            valueCopy -= (digit * tensAccumulator);
        }
    }

    private static void throwUnexpectedCharException(final String formatString, final char c) {
        throw new IllegalArgumentException(format("Unexpected char '{0}' parsing string \"{1}\"", c, formatString));
    }
}
