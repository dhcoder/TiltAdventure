package tiltadv.entity.components.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import tiltadv.entity.AbstractComponent;
import tiltadv.entity.Entity;
import tiltadv.entity.components.TransformComponent;

import static dhcoder.support.utils.StringUtils.format;

/**
 * Simple utility class that shows the current framerate.
 */
public class FpsComponent extends AbstractComponent {

    private final BitmapFont font;
    private TransformComponent transformComponent;

    public FpsComponent(final BitmapFont font) {
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
        font.draw(batch, format("FPS: {0}", fpsString), transformComponent.translate.x, transformComponent.translate.y);
    }
}
