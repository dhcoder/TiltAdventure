package dhcoder.support.math;

import java.util.Random;

/**
* Like {@link CompassDirection} but with only 4 directions.
*/
public enum CardinalDirection {
    E,
    N,
    W,
    S;

    private static final Random RANDOM = new Random();
    private static final CardinalDirection[] CACHED = values();
    private static final Angle[] ANGLES = new Angle[CACHED.length];

    static {
        float angle = 0f;
        for (int i = 0; i < ANGLES.length; i++) {
            ANGLES[i] = Angle.fromDegrees(angle);
            angle += 45f;
        }
    }

    public static CardinalDirection getRandom() {
        return CACHED[RANDOM.nextInt(CACHED.length)];
    }

    public static CardinalDirection getForAngle(final Angle angle) {
        int directionIndex = (int)(angle.getDegrees() + 45f) / 90;
        directionIndex %= CACHED.length;
        return CACHED[directionIndex];
    }

    public boolean isFacing(final Angle angle) {
        float distanceFromAngle = Math.abs(ANGLES[this.ordinal()].getDegrees() - angle.getDegrees());
        float allowedDistance = 45f;

        return distanceFromAngle <= allowedDistance;
    }
}
