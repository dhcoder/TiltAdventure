package tiltadv.components.behavior;

import com.badlogic.gdx.math.Vector2;
import dhcoder.libgdx.entity.Entity;
import dhcoder.support.time.Duration;
import tiltadv.components.math.LerpComponent;
import tiltadv.components.box2d.BodyComponent;
import tiltadv.memory.Pools;

/**
* Class that oscillates an entity between two locations, both easing out of and into each location.
*/
public final class OscillationBehaviorComponent extends LerpComponent {

    private final Vector2 from = new Vector2();
    private final Vector2 to = new Vector2();
    private BodyComponent bodyComponent;

    public OscillationBehaviorComponent() {
        super();
        setShouldLoop(true);
        setActive(true);
    }

    public OscillationBehaviorComponent setOscillation(final Vector2 from, final Vector2 to, final Duration duration) {
        this.from.set(from);
        this.to.set(to);
        setDuration(duration);
        return this;
    }

    @Override
    protected void handleLerp(final float percent) {
        Vector2 currPos = Pools.vector2s.grabNew();
        currPos.set(from);
        currPos.lerp(to, percent);
        bodyComponent.setDesiredPosition(currPos);
        Pools.vector2s.freeCount(1);
    }

    @Override
    protected void handleInitialize(final Entity owner) {
        bodyComponent = owner.requireComponentAfter(this, BodyComponent.class);
    }

    @Override
    public void handleReset() {
        from.setZero();
        to.setZero();
        bodyComponent = null;
    }

}
