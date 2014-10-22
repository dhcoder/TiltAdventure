package tiltadv.components.body;

import com.badlogic.gdx.math.Vector2;
import dhcoder.libgdx.entity.AbstractComponent;
import dhcoder.libgdx.entity.Entity;
import dhcoder.libgdx.render.RenderSystem;
import dhcoder.support.time.Duration;
import tiltadv.globals.Services;

import static dhcoder.support.contract.ContractUtils.requireNonNull;

/**
 * Camera that follows an entity.
 */
public final class FollowCameraComponent extends AbstractComponent {
    private Entity targetEntity;
    private RenderSystem renderSystem;

    public FollowCameraComponent setTargetEntity(final Entity targetEntity) {
        this.targetEntity = targetEntity;
        return this;
    }

    @Override
    public void initialize(final Entity owner) {
        requireNonNull(targetEntity, "Camera must follow a target entity.");
        renderSystem = Services.get(RenderSystem.class);
    }

    @Override
    public void update(final Duration elapsedTime) {
        final Vector2 position = targetEntity.requireComponent(PositionComponent.class).getPosition();
        renderSystem.setOffset(position);
    }

    @Override
    public void reset() {
        targetEntity = null;
        renderSystem = null;
    }
}
