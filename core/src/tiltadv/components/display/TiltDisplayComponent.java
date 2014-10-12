package tiltadv.components.display;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import dhcoder.libgdx.entity.AbstractComponent;
import dhcoder.libgdx.entity.Entity;
import dhcoder.support.math.Angle;
import dhcoder.support.opt.Opt;
import dhcoder.support.time.Duration;
import tiltadv.components.body.TiltComponent;
import tiltadv.memory.Pools;

/**
 * Component which sets the transform of an entity to match the direction of the hardware's tilt.
 */
public final class TiltDisplayComponent extends AbstractComponent {

    private TextureRegion arrowSprite;

    private SpriteComponent spriteComponent;
    private final Opt<TiltComponent> tiltComponentOpt = Opt.withNoValue();

    /**
     * Create a tilt indicator by passing in a sprite which represents an arrow facing straight right. This component
     * will rotate and render the arrow appropriately.
     */
    public TiltDisplayComponent setTextureRegion(final TextureRegion arrowTexture) {
        this.arrowSprite = arrowTexture;
        return this;
    }

    public TiltDisplayComponent setTargetEntity(final Entity targetEntity) {
        tiltComponentOpt.set(targetEntity.requireComponent(TiltComponent.class));
        return this;
    }

    @Override
    public void initialize(final Entity owner) {
        spriteComponent = owner.requireComponent(SpriteComponent.class);
        spriteComponent.setTextureRegion(arrowSprite);
    }

    @Override
    public void update(final Duration elapsedTime) {
        if (!tiltComponentOpt.hasValue()) {
            return;
        }

        Vector2 tilt = tiltComponentOpt.getValue().getTilt();
        if (tilt.isZero()) {
            spriteComponent.setHidden(true);
            return;
        }

        spriteComponent.setHidden(false);
        {
            Angle angle = Pools.angles.grabNew();
            angle.setDegrees(tilt.angle());
            spriteComponent.setRotation(angle);
            Pools.angles.free(angle);
        }
    }

    @Override
    public void reset() {
        arrowSprite = null;

        spriteComponent = null;
        tiltComponentOpt.clear();
    }
}
