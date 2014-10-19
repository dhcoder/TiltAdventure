package tiltadv.globals;

/**
 * Various developer settings for the game. By convention, these settings should all be ignored if {@link
 * DevSettings#IN_DEV_MODE} is {@code false}.
 */
public final class DevSettings {

    /**
     * If the app is in dev mode, we may enable debug features, log more verbosely, and write less robust algorithms so
     * we can crash earlier. By convention, any settings in {@link DevSettings} should be ignored if we're not in dev
     * mode.
     * <p/>
     * Add "-Dtiltadv.dev=true" to the VM options for the launcher of this application to put the app in dev mode.
     */
    public static final boolean IN_DEV_MODE = Boolean.getBoolean("tiltadv.dev");

    /**
     * If {@code true} and in dev mode, we should render shape overlays over all entities set up for collision.
     */
    public static boolean SHOW_COLLISION_SHAPES = true;

    /**
     * If {@code true} and in dev mode, we should run as many sanity checks as possible (verifying expected data but
     * likely slowing down the game).
     */
    public static boolean RUN_SANITY_CHECKS = true;

    /**
     * If in dev mode, apply a slow-mo multiplier - setting to {@code 4f} means 4x slowdown.
     */
    public static float SLOW_MO_FACTOR = 3f;

    private DevSettings() {}

}
