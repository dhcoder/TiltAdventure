package tiltadv.globals;

/**
 * Various developer settings for the game. By convention, these settings should all be ignored if {@link
 * Settings#IN_DEV_MODE} is {@code false}.
 */
public final class DevSettings {

    /**
     * If true and in dev mode, we should render shape overlays over all entities set up for collision.
     */
    public static boolean SHOW_COLLISION_SHAPES = true;

    private DevSettings() {}

}
