package tiltadv;

import tiltadv.util.lambda.Action0;

import static junit.framework.TestCase.fail;
import static tiltadv.util.StringUtils.format;

public class TestUtils {

    /**
     * Confirm that an expected exception happens when the passed in action is run or fail the test.
     *
     * @param reason         A message which explains what should have happened in case the test fails.
     * @param exceptionClass The type of exception which should get thrown.
     * @param action         A method which should throw an exception as a side-effect of being run.
     */
    public static void assertException(final String reason, final Class<? extends Exception> exceptionClass,
        final Action0 action) {

        boolean wasExceptionThrown = false;

        try {
            action.run();
        } catch (Exception e) {
            wasExceptionThrown = e.getClass() == exceptionClass;
        }

        if (!wasExceptionThrown) {
            String finalReason = format("Expected exception: {0}\n{1}", exceptionClass, reason);
            fail(finalReason);
        }
    }
}
