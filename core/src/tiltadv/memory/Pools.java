package tiltadv.memory;

import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import dhcoder.libgdx.pool.QuaternionPoolBuilder;
import dhcoder.libgdx.pool.Vector2PoolBuilder;
import dhcoder.libgdx.pool.Vector3PoolBuilder;
import dhcoder.support.math.Angle;
import dhcoder.support.memory.Pool;
import dhcoder.support.opt.Opt;
import dhcoder.support.time.Duration;

/**
 * A collection of memory pools used by our game. These pools are meant for short, quick allocations, not long-lived
 * instances.
 */
public final class Pools {

    public static final Pool<Angle> angles = Pool.of(Angle.class);
    public static final Pool<Duration> durations = Pool.of(Duration.class);
    public static final Pool<Quaternion> quaternions = QuaternionPoolBuilder.build();
    public static final Pool<Vector2> vector2s = Vector2PoolBuilder.build();
    public static final Pool<Vector3> vector3s = Vector3PoolBuilder.build();
    public static final Pool<Opt> opts = Pool.of(Opt.class);

    private Pools() {
        // Do not instantiate directly, this is essentially a data class
    }
}
