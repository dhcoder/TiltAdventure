package tiltadv.memory;

import com.badlogic.gdx.math.Vector2;
import dhcoder.libgdx.pool.VectorPoolBuilder;
import dhcoder.support.math.Angle;
import dhcoder.support.memory.Pool;
import dhcoder.support.time.Duration;

/**
 * A collection of all memory pools used by our game.
 */
public final class Pools {

    public static final Pool<Angle> angles = Pool.of(Angle.class);
    public static final Pool<Duration> durations = Pool.of(Duration.class);
    public static final Pool<Vector2> vectors = VectorPoolBuilder.build();

    private Pools() {
        // Do not instantiate directly, this is essentially a data class
    }
}
