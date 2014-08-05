package tiltadv.globals;

import dhcoder.support.collision.CollisionSystem;

/**
 * Collection of collision groups. See also {@link CollisionSystem}.
 */
public final class Group {
    public static final int OBSTACLES = 1 << 0;
    public static final int PLAYER = 1 << 1;
}