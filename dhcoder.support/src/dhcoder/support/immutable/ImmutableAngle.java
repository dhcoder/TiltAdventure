package dhcoder.support.immutable;

import dhcoder.support.math.Angle;

/**
 * A class that wraps an Angle, offering read-only access to it.
 */
public class ImmutableAngle extends Immutable<Angle> {

    public ImmutableAngle(final Angle angle) {
        super(angle);
    }

    @Override
    public Angle toMutable() {
        Angle angleCopy = new Angle();
        angleCopy.setFrom(wrappedMutable);
        return angleCopy;
    }

    @Override
    public void copyInto(final Angle target) {
        target.setFrom(wrappedMutable);
    }

    public float getDegrees() {
        return wrappedMutable.getDegrees();
    }

    public float getRadians() {
        return wrappedMutable.getRadians();
    }
}
