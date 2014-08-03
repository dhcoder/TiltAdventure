package tiltadv.memory;

import com.badlogic.gdx.math.Vector2;
import dhcoder.support.math.Angle;
import dhcoder.support.memory.Pool;
import dhcoder.support.time.Duration;

/**
 * A collection of all memory pools used by our game.
 */
public final class Pools {

    public static final Pool<Angle> angles = Pool.of(Angle.class);
    public static final Pool<Duration> durations = Pool.of(Duration.class);
    public static final Pool<Vector2> vectors = new Pool<Vector2>(new Pool.AllocateMethod<Vector2>() {
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
