package dhcoder.support.math;

import java.util.Random;

/**
* Enumeration for compass directions and various utility methods.
*/
public enum CompassDirection {
    E,
    NE,
    N,
    NW,
    W,
    SW,
    S,
    SE;

    private static final Random RANDOM = new Random();
    private static final CompassDirection[] CACHED = values();
    private static final Angle[] ANGLES = new Angle[CACHED.length];

    static {
        float angle = 0f;
        for (int i = 0; i < ANGLES.length; i++) {
            ANGLES[i] = Angle.fromDegrees(angle);
            angle += 45.0f;
        }
    }

    public static CompassDirection getRandom() {
        return CACHED[RANDOM.nextInt(CACHED.length)];
    }

    public static CompassDirection getForAngle(final Angle angle) {
        int directionIndex = (int)(angle.getDegrees() + 22.5f) / 45;
        directionIndex %= CACHED.length;
        return CACHED[directionIndex];
    }

    public boolean isFacing(final Angle angle) {
        float distanceFromAngle = Math.abs(ANGLES[this.ordinal()].getDegrees() - angle.getDegrees());
        float allowedDistance = 22.5f;

        return distanceFromAngle <= allowedDistance;
    }
}
