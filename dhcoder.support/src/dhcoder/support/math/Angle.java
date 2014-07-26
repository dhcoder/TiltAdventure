package dhcoder.support.math;

import dhcoder.support.opt.OptFloat;

import static dhcoder.support.utils.StringUtils.format;

/**
 * Simple class that represents a 0 -> 360° angle. You can set and get this angle's value in either degreesOpt or
 * radiansOpt.
 */
public final class Angle {

    /**
     * A float version of java.lang.Math.PI
     */
    public static final float PI = (float)Math.PI;

    /**
     * Convenience constant for π/2
     */
    public static final float HALF_PI = PI / 2f;

    /**
     * Convenience constant for 2π
     */
    public static final float TWO_PI = PI * 2f;

    /**
     * Multiplying this to a value in degrees converts it to radians.
     */
    public static final float RAD_TO_DEG = 180f / PI;

    /**
     * Multiplying this to a value in radians converts it to degrees.
     */
    public static final float DEG_TO_RAD = PI / 180f;
    private static final float FULL_REVOLUTION_RAD = 2 * PI;
    private static final float FULL_REVOLUTION_DEG = 360f;

    public static Angle fromDegrees(final float degrees) {
        Angle angle = new Angle();
        angle.setDegrees(degrees);
        return angle;
    }

    public static Angle fromRadians(final float radians) {
        Angle angle = new Angle();
        angle.setRadians(radians);
        return angle;
    }

    // One or both of these values are guaranteed to be set at any time. When one value is set, the other invalidated,
    // but when a request is made to get an unset value, it will lazily be calculated at that time.
    private final OptFloat degreesOpt = OptFloat.of(0f);
    private final OptFloat radiansOpt = OptFloat.of(0f);

    public float getDegrees() {
        if (!degreesOpt.hasValue()) {
            degreesOpt.set(radiansOpt.getValue() * RAD_TO_DEG);
        }
        return degreesOpt.getValue();
    }

    public void setDegrees(final float degrees) {
        float boundedDegrees = degrees % FULL_REVOLUTION_DEG;
        while (boundedDegrees < 0f) {
            boundedDegrees += FULL_REVOLUTION_DEG;
        }

        degreesOpt.set(boundedDegrees);
        radiansOpt.clear();
    }

    public float getRadians() {
        if (!radiansOpt.hasValue()) {
            radiansOpt.set(degreesOpt.getValue() * DEG_TO_RAD);
        }
        return radiansOpt.getValue();
    }

    public void setRadians(final float radians) {
        float boundedRadians = radians % FULL_REVOLUTION_RAD;
        while (boundedRadians < 0f) {
            boundedRadians += FULL_REVOLUTION_RAD;
        }

        radiansOpt.set(boundedRadians);
        degreesOpt.clear();
    }

    public void setFrom(final Angle rhs) {
        degreesOpt.setFrom(rhs.degreesOpt);
        radiansOpt.setFrom(rhs.radiansOpt);
    }

    @Override
    public String toString() {
        return format("{0}°", getDegrees());
    }
}
