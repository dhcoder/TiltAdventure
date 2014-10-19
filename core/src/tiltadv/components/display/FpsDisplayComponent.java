package tiltadv.components.display;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Vector2;
import dhcoder.libgdx.entity.AbstractComponent;
import dhcoder.libgdx.entity.Entity;
import dhcoder.libgdx.render.RenderSystem;
import dhcoder.libgdx.render.Renderable;
import dhcoder.support.time.Duration;
import tiltadv.components.body.PositionComponent;
import tiltadv.globals.RenderLayers;
import tiltadv.globals.Services;

import static dhcoder.support.text.StringUtils.format;

/**
 * Simple utility class that shows the current framerate.
 */
public final class FpsDisplayComponent extends AbstractComponent implements Renderable {

    private final static String FPS_LABEL_FORMAT = "FPS: {0}";
    private final String[] fpsLabels;
    private BitmapFont font;
    private PositionComponent positionComponent;
    private String fpsLabel;

    public FpsDisplayComponent() {
        fpsLabels = new String[61];
        fpsLabels[0] = format(FPS_LABEL_FORMAT, "--");
        for (int i = 1; i <= 60; ++i) {
            fpsLabels[i] = format(FPS_LABEL_FORMAT, i);
        }
    }

    public FpsDisplayComponent set(final BitmapFont font) {
        this.font = font;
        return this;
    }

    @Override
    public void initialize(final Entity owner) {
        positionComponent = owner.requireComponent(PositionComponent.class);

        RenderSystem renderSystem = Services.get(RenderLayers.class).getUiRenderLayer();
        renderSystem.add(this);
    }

    @Override
    public void update(final Duration elapsedTime) {
        int fps = Math.min(Gdx.graphics.getFramesPerSecond(), 60);
        fpsLabel = fpsLabels[fps];
    }

    @Override
    public void render(final Batch batch) {
        Vector2 translate = positionComponent.getPosition();
        font.draw(batch, fpsLabel, translate.x, translate.y);
    }

    @Override
    public void reset() {
        positionComponent = null;
        fpsLabel = null;
        font = null;

        RenderSystem renderSystem = Services.get(RenderLayers.class).getGameRenderLayer();
        renderSystem.remove(this);
    }
}
