package tiltadv.globals;

import com.badlogic.gdx.physics.box2d.Filter;

/**
 * Collection of collision groups. Used with Box2D {@link Filter}s.
 */
public final class Category {
    public static final int OBSTACLES = 1 << 0;
    public static final int PLAYER = 1 << 1;
    public static final int ENEMY = 1 << 2;
    public static final int ENEMY_PROJECTILE = 1 << 3;
    public static final int PLAYER_SENSOR = 1 << 4;
    public static final int SWORD = 1 << 5;
    public static final int GRAVITY_WELL = 1 << 6;
}
