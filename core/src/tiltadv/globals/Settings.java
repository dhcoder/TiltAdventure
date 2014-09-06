package tiltadv.globals;

/**
 * Various global settings that modify the shipped game. See also {@link DevSettings}, as that is an appropriate place
 * for settings that should NEVER launch with the game.
 */
public final class Settings {

    /**
     * If the app is in dev mode, we may enable debug features, log more verbosely, and write less robust algorithms so
     * we can crash earlier. By convention, any settings in {@link DevSettings} should be ignored if we're not in dev
     * mode.
     * <p/>
     * Add "-Dtiltadv.dev=true" to the VM options for the launcher of this application to put the app in dev mode.
     */
    public static final boolean IN_DEV_MODE = Boolean.getBoolean("tiltadv.dev");

    /**
     * Whether the game should be allowed to vibrate.
     */
    public static final boolean VIBRATION_ENABLED = true;

    private Settings() {}

}
