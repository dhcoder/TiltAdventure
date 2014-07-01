package tiltadv.util.lambda;

import java.util.List;

/**
 * Interface for a callback that returns a value.
 *
 * @param <R> The type of the return value
 */
public interface Func0<R> {
    R run();
}