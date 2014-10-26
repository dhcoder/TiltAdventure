package tiltadv.components.body;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import dhcoder.libgdx.entity.AbstractComponent;
import dhcoder.libgdx.entity.Entity;
import dhcoder.libgdx.render.RenderSystem;
import dhcoder.support.time.Duration;
import tiltadv.assets.Scene;
import tiltadv.assets.SceneDatastore;
import tiltadv.globals.Services;
import tiltadv.memory.Pools;

import static dhcoder.support.contract.ContractUtils.requireNonNull;
import static dhcoder.support.math.MathUtils.clamp;

/**
 * Camera that follows an entity.
 */
public final class FollowCameraComponent extends AbstractComponent {
    private final Vector2 min = new Vector2();
    private final Vector2 max = new Vector2();

    private PositionComponent targetPositionComponent;
    private RenderSystem renderSystem;

    public FollowCameraComponent setTargetEntity(final Entity targetEntity) {
        targetPositionComponent = targetEntity.requireComponent(PositionComponent.class);
        return this;
    }

    @Override
    public void initialize(final Entity owner) {
        requireNonNull(targetPositionComponent, "Camera must follow a target entity.");
        renderSystem = Services.get(RenderSystem.class);

        updateBounds();
    }

    private void updateBounds() {
        Scene scene = Services.get(SceneDatastore.class).get("demo");
        RenderSystem renderSystem = Services.get(RenderSystem.class);
        final float halfScreenW = renderSystem.getCamera().viewportWidth / 2f;
        final float halfScreenH = renderSystem.getCamera().viewportHeight / 2f;
        min.x = scene.getLeftX() + halfScreenW;
        max.x = scene.getRightX() - halfScreenW;
        min.y = scene.getBottomY() + halfScreenH;
        max.y = scene.getTopY() - halfScreenH;
    }

    @Override
    public void update(final Duration elapsedTime) {

        final Vector2 offset = Pools.vector2s.grabNew();
        offset.set(targetPositionComponent.getPosition());

        offset.x = clamp(offset.x, min.x, max.x);
        offset.y = clamp(offset.y, min.y, max.y);

        renderSystem.setOffset(offset);

        Pools.vector2s.freeCount(1);
    }

    @Override
    public void reset() {
        targetPositionComponent = null;
        renderSystem = null;
        min.setZero();
        max.setZero();
    }
}
