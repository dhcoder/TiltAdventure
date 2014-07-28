package dhcoder.support.utils;

import static dhcoder.support.utils.StringUtils.format;

public final class MathUtils {

    public static int clamp(final int value, final int min, final int max) {
        if (min > max) {
            throw new IllegalArgumentException(format("Called clamp with min < max (min: {0}, max: {1})", min, max));
        }
        return Math.max(min, Math.min(max, value));
    }

    public static float clamp(final float value, final float min, final float max) {
        if (min > max) {
            throw new IllegalArgumentException(format("Called clamp with min < max (min: {0}, max: {1})", min, max));
        }
        return Math.max(min, Math.min(max, value));
    }

    private MathUtils() {} // Disabled constructor
}
