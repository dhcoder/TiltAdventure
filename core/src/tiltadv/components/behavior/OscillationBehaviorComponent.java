package tiltadv.components.behavior;

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import dhcoder.libgdx.entity.AbstractComponent;
import dhcoder.libgdx.entity.Entity;
import dhcoder.support.math.Angle;
import dhcoder.support.time.Duration;
import tiltadv.components.model.TransformComponent;
import tiltadv.memory.Pools;

import static com.badlogic.gdx.math.MathUtils.sin;

/**
 * Class that oscillates an entity between two locations, both easing out of and into each location.
 */
public final class OscillationBehaviorComponent extends AbstractComponent {

    private final float xFrom;
    private final float yFrom;
    private final float xTo;
    private final float yTo;
    private final Duration duration;
    private final Duration accumulated = Duration.zero();
    private boolean onReturnTrip;
    private TransformComponent transformComponent;

    public OscillationBehaviorComponent(final float xFrom, final float yFrom, final float xTo, final float yTo,
        final Duration duration) {

        this.xFrom = xFrom;
        this.yFrom = yFrom;
        this.xTo = xTo;
        this.yTo = yTo;
        this.duration = duration;
    }

    @Override
    public void initialize(final Entity owner) {
        transformComponent = owner.requireComponent(TransformComponent.class);
    }

    @Override
    public void update(final Duration elapsedTime) {
        accumulated.add(elapsedTime);
        if (accumulated.getSeconds() > duration.getSeconds()) {
            accumulated.subtract(duration);
            onReturnTrip = !onReturnTrip;
        }

        float percent = accumulated.getSeconds() / duration.getSeconds();

        float xStart = onReturnTrip ? xFrom : xTo;
        float yStart = onReturnTrip ? yFrom : yTo;
        float xEnd = onReturnTrip ? xTo : xFrom;
        float yEnd = onReturnTrip ? yTo : yFrom;

        Vector2 currPos = Pools.vectors.grabNew();
        Vector2 destPos = Pools.vectors.grabNew();
        currPos.set(xStart, yStart);
        destPos.set(xEnd, yEnd);
        currPos.interpolate(destPos, percent, Interpolation.sine);
        transformComponent.setTranslate(currPos);
        Pools.vectors.free(destPos);
        Pools.vectors.free(currPos);
    }
}
