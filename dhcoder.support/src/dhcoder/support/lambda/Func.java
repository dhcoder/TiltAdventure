package dhcoder.support.lambda;

/**
 * Interface for a callback that returns a value.
 *
 * @param <R> The type of the return value
 */
public interface Func<R> {
    R run();
}