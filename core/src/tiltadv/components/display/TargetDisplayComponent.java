package tiltadv.components.display;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import dhcoder.libgdx.entity.AbstractComponent;
import dhcoder.libgdx.entity.Entity;
import dhcoder.support.event.EventListener;
import dhcoder.support.math.Angle;
import dhcoder.support.opt.Opt;
import dhcoder.support.time.Duration;
import tiltadv.components.body.PositionComponent;
import tiltadv.components.hierarchy.OffsetComponent;
import tiltadv.globals.Events;
import tiltadv.memory.Pools;

import static dhcoder.support.contract.ContractUtils.requireNonNull;

/**
 * Component that renders a character which can move in any of the cardinal directions.
 */
public final class TargetDisplayComponent extends AbstractComponent {


    private final static Duration LOOP_DURATION = Duration.fromSeconds(.8f);
    private final static float TARGET_RADIUS = 15f;

    private final Angle angle = Angle.fromDegrees(0f);

    private Duration elapsedSoFar = Duration.zero();

    private TextureRegion textureRegion;

    private Opt<Entity> targetEntityOpt = Opt.withNoValue();

    private OffsetComponent offsetComponent;
    private final EventListener<Entity> onTargetSelected = new EventListener<Entity>() {
        @Override
        public void run(final Entity sender) {
            targetEntityOpt.set(sender);
            offsetComponent.setSourcePosition(sender.requireComponent(PositionComponent.class));
            spriteComponent.setHidden(false);

        }
    };;
    private final EventListener<Entity> onTargetCleared = new EventListener<Entity>() {
        @Override
        public void run(final Entity sender) {
            targetEntityOpt.clear();
            offsetComponent.clearSourcePosition();
            spriteComponent.setHidden(true);
        }
    };;
    private SpriteComponent spriteComponent;

    public void setTextureRegion(final TextureRegion textureRegion) {
        this.textureRegion = textureRegion;
    }

    @Override
    public void initialize(final Entity owner) {
        requireNonNull(textureRegion, "Target texture must be set");
        spriteComponent = owner.requireComponent(SpriteComponent.class);
        spriteComponent.setTextureRegion(textureRegion);
        spriteComponent.setHidden(true);
        spriteComponent.setZ(1000f);

        offsetComponent = owner.requireComponent(OffsetComponent.class);

        Events.onTargetSelected.addListener(onTargetSelected);
        Events.onTargetCleared.addListener(onTargetCleared);
    }

    @Override
    public void update(final Duration elapsedTime) {
        elapsedSoFar.add(elapsedTime);

        while (elapsedSoFar.getSeconds() > LOOP_DURATION.getSeconds()) {
            elapsedSoFar.subtract(LOOP_DURATION);
        }

        if (!targetEntityOpt.hasValue()) {
            return;
        }

        angle.setDegrees(elapsedSoFar.getSeconds() / LOOP_DURATION.getSeconds() * 360f);
        Vector2 offset = Pools.vector2s.grabNew().set(TARGET_RADIUS, 0f);
        offset.rotate(angle.getDegrees());
        offsetComponent.setOffset(offset);
        Pools.vector2s.freeCount(1);
    }

    @Override
    public void reset() {
        elapsedSoFar.setZero();
        targetEntityOpt.clear();
        angle.setDegrees(0f);
    }

}
