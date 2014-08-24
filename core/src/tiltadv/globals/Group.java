package tiltadv.globals;

import dhcoder.libgdx.collision.CollisionSystem;

/**
 * Collection of collision groups. See also {@link CollisionSystem}.
 */
public final class Group {
    public static final int OBSTACLES = 1 << 0;
    public static final int PLAYER = 1 << 1;
    public static final int ENEMY = 1 << 2;
    public static final int ENEMY_PROJECTILE = 1 << 3;
}
