package tiltadv.components.behavior;

import com.badlogic.gdx.math.Vector2;
import dhcoder.libgdx.entity.AbstractComponent;
import dhcoder.libgdx.entity.Entity;
import dhcoder.support.time.Duration;
import tiltadv.components.body.HeadingComponent;
import tiltadv.components.collision.PlayerCollisionComponent;
import tiltadv.components.collision.PlayerSensorCollisionComponent;
import tiltadv.components.display.SpriteComponent;
import tiltadv.components.hierarchy.OffsetComponent;
import tiltadv.components.hierarchy.ParentComponent;
import tiltadv.memory.Pools;

/**
 * Component that maintains the collision logic for the main player's avatar.
 */
public final class PlayerSensorBehaviorComponent extends AbstractComponent {

    private float x;
    private OffsetComponent offsetComponent;
    private HeadingComponent headingComponent;

    @Override
    public void initialize(final Entity owner) {
        Entity player = owner.requireComponent(ParentComponent.class).getParent();
        headingComponent = player.requireComponent(HeadingComponent.class);
        offsetComponent = owner.requireComponent(OffsetComponent.class);

        x = player.requireComponent(PlayerCollisionComponent.class).getShape().getHalfWidth() +
            owner.requireComponent(PlayerSensorCollisionComponent.class).getShape().getHalfWidth();
        x *= .8f;

        owner.requireComponent(SpriteComponent.class).setZ(-1000f);
    }

    @Override
    public void update(final Duration elapsedTime) {
        int mark = Pools.vector2s.mark();
        Vector2 offset = Pools.vector2s.grabNew().set(x, 0);
        offset.rotate(headingComponent.getHeading().getDegrees());
        offsetComponent.setOffset(offset);
        Pools.vector2s.freeToMark(mark);
    }

    @Override
    public void reset() {
        x = 0f;
        headingComponent = null;
        offsetComponent = null;
    }
}
