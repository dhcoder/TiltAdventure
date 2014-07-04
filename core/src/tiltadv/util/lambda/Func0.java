package tiltadv.util.lambda;

/**
 * Interface for a callback that returns a value.
 *
 * @param <R> The type of the return value
 */
public interface Func0<R> {

    R run();
}