package dhcoder.support.lambda;

/**
 * Interface for a callback that takes three arguments and doesn't return anything.
 */
public interface Action3<T1, T2, T3> {

    void run(T1 arg1, T2 arg2, T3 arg3);
}