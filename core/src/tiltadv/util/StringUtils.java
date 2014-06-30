package tiltadv.util;

public class StringUtils {

    /**
     * Format a string using C# style formatting, i.e. using {0} instead of %0$s.
     *
     * See <a href="http://msdn.microsoft.com/en-us/library/system.string.format.aspx#Format_Brief">Microsoft's
     * String.Format documentation</a> for more information. Note that this method doesn't support numeric formatting,
     * such as {0.2f}.
     *
     * @param input The formatting string.
     * @param args Various args whose string values will be used in the final string.
     */
    public static String format(String input, Object... args) {

        // We want to replace {{ -> { and }} -> }, but we need to worry about {{0}}, which should change to {0} and not
        // ever insert the value of arg[0]. The way we do this here is by creating special intermediate place-holder
        // characters for { and } and convert them at the end.
        // (FYI, this is a somewhat lazy approach - the real way to do this would be to parse the input string
        // ourselves, one character at a time, and build up our final string that way. But this way is much easier and
        // good enough for our current needs.)
        final String ESCAPE_BRACES_L = "{{";
        final String ESCAPE_BRACES_R = "}}";
        // We use hex values 0xFFFE and 0xFFFF because they are guaranteed to never be real characters, see
        // http://en.wikipedia.org/wiki/Mapping_of_Unicode_characters#Non-characters
        final String BRACES_INDEX_L = Character.toString((char)0xFFFE);
        final String BRACES_INDEX_R = Character.toString((char)0xFFFF);

        StringBuilder stringBuilder = new StringBuilder(input);

        replace(stringBuilder, ESCAPE_BRACES_L, BRACES_INDEX_L);
        replace(stringBuilder, ESCAPE_BRACES_R, BRACES_INDEX_R);

        for (int i = 0; i < args.length; ++i) {
            String indexString = "{" + i + "}";
            String argString = args[i].toString();
            replace(stringBuilder, indexString, argString);
        }

        replace(stringBuilder, BRACES_INDEX_L, "{");
        replace(stringBuilder, BRACES_INDEX_R, "}");

        return stringBuilder.toString();
    }

    private static void replace(StringBuilder builder, String from, String to) {
        int insertPosition;
        int fromIndex = 0;
        while ((insertPosition = builder.indexOf(from, fromIndex)) >= 0) {
            builder.replace(insertPosition, insertPosition + from.length(), to);
            fromIndex = insertPosition + to.length();
        }
    }
}
