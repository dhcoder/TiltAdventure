package tiltadv.immutable;

import dhcoder.support.math.Angle;

/**
 * A class that wraps an Angle, offering read-only access to it.
 */
public class ImmutableAngle {

    private final Angle innerAngle;

    public ImmutableAngle(final Angle angle) {
        this.innerAngle = angle;
    }

    public Angle toAngle() {
        Angle angleCopy = new Angle();
        angleCopy.set(innerAngle);
        return angleCopy;
    }

    public float getDegrees() {
        return innerAngle.getDegrees();
    }

    public float getRadians() {
        return innerAngle.getRadians();
    }

    @Override
    public String toString() {
        return innerAngle.toString();
    }
}
