package dhcoder.test;

import static junit.framework.TestCase.fail;

public final class TestUtils {

    /**
     * Confirm that an expected exception happens when the passed in action is run or fail the test.
     *
     * @param reason         A message which explains what should have happened in case the test fails.
     * @param exceptionClass The type of exception which should get thrown.
     * @param codeUnderTest  A method which should throw an exception as a side-effect of being run.
     */
    public static void assertException(final String reason, final Class<? extends Exception> exceptionClass,
        final Runnable codeUnderTest) {

        Exception exceptionThrown = null;

        try {
            codeUnderTest.run();
        } catch (Exception e) {
            exceptionThrown = e;
        }

        if (exceptionThrown == null || exceptionThrown.getClass() != exceptionClass) {
            StringBuilder builder = new StringBuilder();
            builder.append(String.format("%1$s\n", reason));
            builder.append(String.format("Expected: %1$s\n", exceptionClass));
            if (exceptionThrown != null) {
                builder.append(String.format("Actual: %1$s", exceptionThrown.getClass()));
            }
            fail(builder.toString());
        }
    }
}
