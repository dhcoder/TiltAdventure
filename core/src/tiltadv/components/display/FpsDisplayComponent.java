package tiltadv.components.display;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Vector2;
import dhcoder.libgdx.entity.AbstractComponent;
import dhcoder.libgdx.entity.Entity;
import dhcoder.support.time.Duration;
import tiltadv.components.model.TransformComponent;

import static dhcoder.support.text.StringUtils.format;

/**
 * Simple utility class that shows the current framerate.
 */
public final class FpsDisplayComponent extends AbstractComponent {

    private final static String FPS_LABEL_FORMAT = "FPS: {0}";
    private final String[] fpsLabels;
    private BitmapFont font;
    private TransformComponent transformComponent;
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
        transformComponent = owner.requireComponent(TransformComponent.class);
    }

    @Override
    public void update(final Duration elapsedTime) {
        int fps = Math.min(Gdx.graphics.getFramesPerSecond(), 60);
        fpsLabel = fpsLabels[fps];
    }

    @Override
    public void render(final Batch batch) {
        Vector2 translate = transformComponent.getTranslate();
        font.draw(batch, fpsLabel, translate.x, translate.y);
    }

    @Override
    public void reset() {
        transformComponent = null;
        fpsLabel = null;
        font = null;
    }
}
