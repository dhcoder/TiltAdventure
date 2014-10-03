package tiltadv.components.body;

import com.badlogic.gdx.math.Vector2;
import dhcoder.libgdx.entity.AbstractComponent;
import dhcoder.libgdx.entity.Entity;
import dhcoder.support.math.Angle;

/**
 * Component that encapsulates the logic of calculating an {@link Entity}'s velocity and acceleration. Expects the
 * existence of a {@link PositionComponent} to act upon.
 * <p/>
 * TODO: Add tests and documentation, maybe also remove some public methods here.
 */
public final class HeadingComponent extends AbstractComponent {

    private final Angle heading = Angle.fromDegrees(0f);

    public Angle getHeading() { return heading; }

    public HeadingComponent setHeading(final Angle heading) {
        this.heading.setFrom(heading);
        return this;
    }

    public HeadingComponent setHeadingFrom(final Vector2 vector) {
        this.heading.setDegrees(vector.angle());
        return this;
    }

    @Override
    public void reset() {
        heading.setDegrees(0f);
    }
}