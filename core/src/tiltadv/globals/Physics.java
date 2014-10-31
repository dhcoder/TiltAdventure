package tiltadv.globals;

/**
 * Helper methods for our physics system.
 */
public final class Physics {
    public static final float PIXELS_TO_METERS = 0.0625f; // Main character is 24 pixels / 1.5 meters high
    public static final float METERS_TO_PIXELS = 1 / PIXELS_TO_METERS;

    public static float toPixels(final float meters) {
        return meters * METERS_TO_PIXELS;
    }

    public static float toMeters(final float pixels) {
        return pixels * PIXELS_TO_METERS;
    }
}
