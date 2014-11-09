package tiltadv.components.behavior;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import dhcoder.libgdx.entity.AbstractComponent;
import dhcoder.libgdx.entity.Entity;
import dhcoder.libgdx.physics.PhysicsSystem;
import dhcoder.libgdx.physics.PhysicsUpdateListener;
import dhcoder.support.event.EventListener;
import dhcoder.support.math.Angle;
import dhcoder.support.opt.Opt;
import dhcoder.support.time.Duration;
import tiltadv.components.display.SpriteComponent;
import tiltadv.components.dynamics.PositionComponent;
import tiltadv.globals.Events;
import tiltadv.globals.Services;
import tiltadv.memory.Pools;

import static dhcoder.support.contract.ContractUtils.requireNonNull;

/**
 * Component that renders an effect which lets you see which Entity has been selected
 */
public final class TargetBehaviorComponent extends AbstractComponent implements PhysicsUpdateListener {
    private final static Duration LOOP_DURATION = Duration.fromSeconds(.8f);
    private final static float TARGET_RADIUS = 15f;

    private final Angle angle = Angle.fromDegrees(0f);
    private final Duration elapsedSoFar = Duration.zero();
    private final Opt<Entity> targetEntityOpt = Opt.withNoValue();
    private TextureRegion textureRegion;
    private PositionComponent positionComponent;
    private SpriteComponent spriteComponent;

    private final EventListener<Entity> onTargetSelected = new EventListener<Entity>() {
        @Override
        public void run(final Entity sender) {
            targetEntityOpt.set(sender);
            spriteComponent.setHidden(false);
        }
    };

    private final EventListener<Entity> onTargetCleared = new EventListener<Entity>() {
        @Override
        public void run(final Entity sender) {
            targetEntityOpt.clear();
            spriteComponent.setHidden(true);
        }
    };

    public void setTextureRegion(final TextureRegion textureRegion) {
        this.textureRegion = textureRegion;
    }

    @Override
    public void initialize(final Entity owner) {
        requireNonNull(textureRegion, "Target texture must be set");
        positionComponent = owner.requireComponentBefore(this, PositionComponent.class);

        spriteComponent = owner.requireComponent(SpriteComponent.class);
        spriteComponent.setTextureRegion(textureRegion);
        spriteComponent.setHidden(true);

        Events.onTargetSelected.addListener(onTargetSelected);
        Events.onTargetCleared.addListener(onTargetCleared);
        Services.get(PhysicsSystem.class).addUpdateListener(this);
    }


    @Override
    public void update(final Duration elapsedTime) {
        elapsedSoFar.add(elapsedTime);

        while (elapsedSoFar.getSeconds() > LOOP_DURATION.getSeconds()) {
            elapsedSoFar.subtract(LOOP_DURATION);
        }
    }

    @Override
    public void onPhysicsUpdate() {
        if (!targetEntityOpt.hasValue()) {
            return;
        }

        angle.setDegrees(elapsedSoFar.getSeconds() / LOOP_DURATION.getSeconds() * 360f);
        Vector2 finalPosition = Pools.vector2s.grabNew().set(TARGET_RADIUS, 0f);
        finalPosition.rotate(angle.getDegrees());
        final Vector2 targetPosition =
            targetEntityOpt.getValue().requireComponent(PositionComponent.class).getPosition();
        finalPosition.add(targetPosition);
        positionComponent.setPosition(finalPosition);
        Pools.vector2s.freeCount(1);
    }

    @Override
    public void reset() {
        Services.get(PhysicsSystem.class).removeUpdateListener(this);
        Events.onTargetSelected.removeListener(onTargetSelected);
        Events.onTargetCleared.removeListener(onTargetCleared);

        angle.setDegrees(0f);
        elapsedSoFar.setZero();
        targetEntityOpt.clear();
        textureRegion = null;
        positionComponent = null;
        spriteComponent = null;
    }

}
