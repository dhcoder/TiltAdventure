package tiltadv.entity.components.display;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Vector2;
import dhcoder.support.time.Duration;
import tiltadv.entity.AbstractComponent;
import tiltadv.entity.Entity;
import tiltadv.entity.components.data.TransformComponent;

import static dhcoder.support.utils.StringUtils.format;

/**
 * Simple utility class that shows the current framerate.
 */
public final class FpsDisplayComponent extends AbstractComponent {

    private final static String FPS_LABEL_FORMAT = "FPS: {0}";
    private final BitmapFont font;
    private TransformComponent transformComponent;

    private String[] fpsLabels;
    private String fpsLabel;

    public FpsDisplayComponent(final BitmapFont font) {
        this.font = font;

        fpsLabels = new String[61];
        fpsLabels[0] = format(FPS_LABEL_FORMAT, "--");
        for (int i = 1; i <= 60; ++i) {
            fpsLabels[i] = format(FPS_LABEL_FORMAT, i);
        }
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
}
