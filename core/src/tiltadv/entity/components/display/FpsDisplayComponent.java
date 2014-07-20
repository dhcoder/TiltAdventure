package tiltadv.entity.components.display;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Vector2;
import tiltadv.entity.AbstractComponent;
import tiltadv.entity.Entity;
import tiltadv.entity.components.data.TransformComponent;

import static dhcoder.support.utils.StringUtils.format;

/**
 * Simple utility class that shows the current framerate.
 */
public class FpsDisplayComponent extends AbstractComponent {

    private final BitmapFont font;
    private TransformComponent transformComponent;

    public FpsDisplayComponent(final BitmapFont font) {
        this.font = font;
    }

    @Override
    public void initialize(final Entity owner) {
        transformComponent = owner.requireComponent(TransformComponent.class);
    }

    @Override
    public void render(final Batch batch) {
        int fps = Gdx.graphics.getFramesPerSecond();
        String fpsString = (fps > 0) ? Integer.toString(fps) : "--";
        Vector2 translate = transformComponent.getTranslate();
        font.draw(batch, format("FPS: {0}", fpsString), translate.x, translate.y);
    }
}
