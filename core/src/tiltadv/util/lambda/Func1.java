package tiltadv.util.lambda;

/**
 * Interface for a callback that takes one argument and returns a value.
 *
 * @param <R> The type of the return value
 */
public interface Func1<R, T> {
    R execute(T arg);
}