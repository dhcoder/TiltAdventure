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
public class FpsDisplayComponent extends AbstractComponent {

    private final static String FPS_LABEL_FORMAT = "FPS: {0}";
    private final BitmapFont font;
    private TransformComponent transformComponent;

    private int lastFps;
    private String fpsLabel;

    public FpsDisplayComponent(final BitmapFont font) {
        this.font = font;
    }

    @Override
    public void initialize(final Entity owner) {
        transformComponent = owner.requireComponent(TransformComponent.class);
        fpsLabel = format(FPS_LABEL_FORMAT, "--");
    }

    @Override
    public void update(final Duration elapsedTime) {
        int fps = Gdx.graphics.getFramesPerSecond();
        if (fps != lastFps) {
            fpsLabel = format(FPS_LABEL_FORMAT, fps);
            lastFps = fps;
        }
    }

    @Override
    public void render(final Batch batch) {
        Vector2 translate = transformComponent.getTranslate();
        font.draw(batch, fpsLabel, translate.x, translate.y);
    }
}
