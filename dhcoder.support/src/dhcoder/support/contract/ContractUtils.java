package dhcoder.support.contract;

/**
 * Simple collection of methods to assert expected values or else throw a {@link IllegalStateException}.
 */
public final class ContractUtils {

    public static void requireNonNull(final Object value, final String message) {
        if (value == null) {
            throw new IllegalStateException(message);
        }
    }

    public static void requireValue(final int value, final int expected, final String message) {
        if (value != expected) {
            throw new IllegalStateException(message);
        }
    }

    public static void requireValue(final float value, final float expected, final String message) {
        if (value != expected) {
            throw new IllegalStateException(message);
        }
    }

}
