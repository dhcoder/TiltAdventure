package dhcoder.support.lambda;

/**
 * Interface for a callback that takes two arguments and doesn't return anything.
 */
public interface Action2<T1, T2> {

    void run(T1 arg1, T2 arg2);
}
