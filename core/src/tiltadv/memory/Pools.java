package tiltadv.memory;

import com.badlogic.gdx.math.Vector2;
import dhcoder.support.memory.Pool;
import dhcoder.support.time.Duration;

/**
 * A collection of all memory pools used by our game.
 */
public class Pools {

    public static final Pool<Duration> duration = new Pool<Duration>(new Pool.AllocateMethod<Duration>() {
        @Override
        public Duration run() {
            return Duration.zero();
        }
    }, new Pool.ResetMethod<Duration>() {
        @Override
        public void run(final Duration item) {
            item.setZero();
        }
    });
    public static final Pool<Vector2> vector = new Pool<Vector2>(new Pool.AllocateMethod<Vector2>() {
        @Override
        public Vector2 run() {
            return new Vector2();
        }
    }, new Pool.ResetMethod<Vector2>() {
        @Override
        public void run(final Vector2 item) {
            item.setZero();
        }
    });

    private Pools() {
        // Do not instantiate directly, this is essentially a data class
    }
}
