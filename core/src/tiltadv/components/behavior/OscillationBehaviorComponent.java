package tiltadv.components.behavior;

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import dhcoder.libgdx.entity.AbstractComponent;
import dhcoder.libgdx.entity.Entity;
import dhcoder.support.time.Duration;
import tiltadv.components.model.TransformComponent;
import tiltadv.memory.Pools;

/**
 * Class that oscillates an entity between two locations, both easing out of and into each location.
 */
public final class OscillationBehaviorComponent extends AbstractComponent {

    private final Vector2 from = new Vector2();
    private final Vector2 to = new Vector2();
    private final Duration duration = Duration.zero();
    private final Duration accumulated = Duration.zero();
    private boolean onReturnTrip;
    private TransformComponent transformComponent;

    public OscillationBehaviorComponent() {
        resetComponent();
    }

    public OscillationBehaviorComponent set(final Vector2 from, final Vector2 to, final Duration duration) {
        this.from.set(from);
        this.to.set(to);
        this.duration.setFrom(duration);
        return this;
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

        Vector2 start = onReturnTrip ? to : from;
        Vector2 end = onReturnTrip ? from : to;

        Vector2 currPos = Pools.vector2s.grabNew();
        currPos.set(start);
        currPos.interpolate(end, percent, Interpolation.sine);
        transformComponent.setTranslate(currPos);
        Pools.vector2s.free(currPos);
    }

    @Override
    protected void resetComponent() {
        from.setZero();
        to.setZero();
        duration.setZero();
        accumulated.setZero();
        onReturnTrip = false;
    }
}
