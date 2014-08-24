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
    // Regions that correspond with each 45° section of the circle
    private static final CardinalDirection[] REGIONS = new CardinalDirection[CACHED.length * 2];
    private static final Angle[] ANGLES = new Angle[CACHED.length];

    static {
        REGIONS[0] = CardinalDirection.E;
        REGIONS[1] = CardinalDirection.N;
        REGIONS[2] = CardinalDirection.N;
        REGIONS[3] = CardinalDirection.W;
        REGIONS[4] = CardinalDirection.W;
        REGIONS[5] = CardinalDirection.S;
        REGIONS[6] = CardinalDirection.S;
        REGIONS[7] = CardinalDirection.E;

        float angleDeg = 0f;
        for (int i = 0; i < ANGLES.length; i++) {
            ANGLES[i] = Angle.fromDegrees(angleDeg);
            angleDeg += 90f; // 360 / 4 directions
        }
    }

    public static CardinalDirection getRandom() {
        return CACHED[RANDOM.nextInt(CACHED.length)];
    }

    public static CardinalDirection getForAngle(final Angle angle) {
        int regionIndex = (int)(angle.getDegrees() / 45f);
        return REGIONS[regionIndex];
    }

    public boolean isFacing(final Angle angle) {
        return getForAngle(angle) == this;
    }

    public Angle getAngle() {
        return ANGLES[this.ordinal()];
    }
}
