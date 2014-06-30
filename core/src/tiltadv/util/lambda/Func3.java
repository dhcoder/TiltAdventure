package tiltadv.util.lambda;

/**
 * Interface for a callback that takes three arguments and returns a value.
 *
 * @param <R> The type of the return value
 */
public interface Func3<R, T1, T2, T3> {
    R execute(T1 arg1, T2 arg2, T3 arg3);
}