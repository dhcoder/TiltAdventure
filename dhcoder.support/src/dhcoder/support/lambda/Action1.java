package dhcoder.support.lambda;

/**
 * Interface for a callback that takes one argument and doesn't return anything.
 */
public interface Action1<T> {

    void run(T arg);
}
