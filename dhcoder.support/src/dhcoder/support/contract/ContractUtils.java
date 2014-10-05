package dhcoder.support.contract;

/**
 * Simple collection of methods to assert expected values or else throw a {@link ContractException}.
 */
public final class ContractUtils {
    public static class ContractException extends RuntimeException {
        public ContractException(final String message) {
            super(message);
        }
    }

    public static void requireNonNull(final Object value, final String message) {
        if (value == null) {
            throw new ContractException(message);
        }
    }

    public static void requireValue(final int expected, final int value, final String message) {
        if (value != expected) {
            throw new ContractException(message);
        }
    }

    public static void requireValue(final float expected, final float value, final String message) {
        if (value != expected) {
            throw new ContractException(message);
        }
    }

    public static void requireTrue(final boolean value, final String message) {
        if (value != true) {
            throw new ContractException(message);
        }
    }

}
