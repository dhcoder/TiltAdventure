package tiltadv;

import tiltadv.util.lambda.Action;

import static junit.framework.TestCase.fail;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

public class TestUtils {

    public static void assertException(String reason, Class<? extends Exception> exceptionClass, Action action) {
        boolean wasExceptionThrown = false;

        try {
            action.execute();
        } catch (Exception e) {
            wasExceptionThrown = e.getClass() == exceptionClass;
        }

        if (!wasExceptionThrown) {
            reason = "Expected exception: " + exceptionClass.toString() + "\n" + reason;
            fail(reason);
        }
    }
}
