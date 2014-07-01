package tiltadv.util.lambda;

/**
 * Interface for a callback that takes two arguments and returns a value.
 *
 * @param <R> The type of the return value
 */
public interface Func2<R, T1, T2> {
    R run(T1 arg1, T2 arg2);
}