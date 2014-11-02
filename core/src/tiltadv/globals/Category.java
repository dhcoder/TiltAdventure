package tiltadv.globals;

import com.badlogic.gdx.physics.box2d.Filter;

/**
 * Collection of collision groups. Used with Box2D {@link Filter}s.
 */
public final class Category {
    public static final short OBSTACLES = 1 << 0;
    public static final short PLAYER = 1 << 1;
    public static final short ENEMY = 1 << 2;
    public static final short ENEMY_PROJECTILE = 1 << 3;
    public static final short PLAYER_SENSOR = 1 << 4;
    public static final short PLAYER_SWORD = 1 << 5;
    public static final short GRAVITY_WELL = 1 << 6;
}
