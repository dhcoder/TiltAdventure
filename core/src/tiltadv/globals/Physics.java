package tiltadv.globals;

import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.PolygonShape;

/**
 * Helper methods for our physics system.
 */
public final class Physics {
    public static final float PIXELS_TO_METERS = 0.0625f; // Main character is 24 pixels / 1.5 meters high
    public static final float METERS_TO_PIXELS = 1 / PIXELS_TO_METERS;

    /**
     * A damping value which, when set, means an entity will come to rest relatively quickly
     */
    public static final float DAMPING_FAST_STOP = 10f;

    public static float toPixels(final float meters) {
        return meters * METERS_TO_PIXELS;
    }

    public static float toMeters(final float pixels) {
        return pixels * PIXELS_TO_METERS;
    }

    public static CircleShape newCircle(final float radiusPixels) {
        CircleShape circleShape = new CircleShape();
        circleShape.setRadius(radiusPixels * PIXELS_TO_METERS);
        return circleShape;
    }

    public static PolygonShape newRectangle(final float halfWidthPixels, final float halfHeightPixels) {
        PolygonShape polygonShape = new PolygonShape();
        polygonShape.setAsBox(halfWidthPixels * PIXELS_TO_METERS, halfHeightPixels * PIXELS_TO_METERS);
        return polygonShape;
    }
}
