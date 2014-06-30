package tiltadv.util.lambda;

/**
 * Interface for a callback that takes three arguments and don't return anything.
 */
public interface Action3<T1, T2, T3> {
    void execute(T1 arg1, T2 arg2, T3 arg3);
}
