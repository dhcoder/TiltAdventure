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
     * If true and in dev mode, we should render shape overlays over all entities set up for collision.
     */
    public static boolean SHOW_COLLISION_SHAPES = true;

    private DevSettings() {}

}
