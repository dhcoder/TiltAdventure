package tiltadv;

import tiltadv.util.lambda.Action0;

import static junit.framework.TestCase.fail;
import static org.hamcrest.MatcherAssert.assertThat;

public class TestUtils {

    public static void assertException(String reason, Class<? extends Exception> exceptionClass, Action0 action) {
        boolean wasExceptionThrown = false;

        try {
            action.run();
        } catch (Exception e) {
            wasExceptionThrown = e.getClass() == exceptionClass;
        }

        if (!wasExceptionThrown) {
            reason = "Expected exception: " + exceptionClass.toString() + "\n" + reason;
            fail(reason);
        }
    }
}
