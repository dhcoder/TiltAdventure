package tiltadv.util.lambda;

/**
 * Interface for a callback that takes one argument and don't return anything.
 */
public interface Action1<T> {
    void execute(T arg);
}
