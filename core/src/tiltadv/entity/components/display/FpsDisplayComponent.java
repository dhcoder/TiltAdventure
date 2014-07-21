package tiltadv.entity.components.display;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Vector2;
import dhcoder.support.memory.MutableInt;
import tiltadv.entity.AbstractComponent;
import tiltadv.entity.Entity;
import tiltadv.entity.components.data.TransformComponent;

import java.nio.CharBuffer;

import static dhcoder.support.utils.StringUtils.format;
import static dhcoder.support.utils.StringUtils.formatInto;

/**
 * Simple utility class that shows the current framerate.
 */
public class FpsDisplayComponent extends AbstractComponent {

    private static final String FPS_LABEL_FORMAT = "FPS: {0d}";
    private static final int ESTIMATED_LABEL_LENGTH = format(FPS_LABEL_FORMAT, 10000).length();

    private final BitmapFont font;
    private TransformComponent transformComponent;
    private final MutableInt fpsValue = new MutableInt();
    private final CharBuffer fpsLabel = CharBuffer.allocate(ESTIMATED_LABEL_LENGTH);

    public FpsDisplayComponent(final BitmapFont font) {
        this.font = font;
    }

    @Override
    public void initialize(final Entity owner) {
        transformComponent = owner.requireComponent(TransformComponent.class);
    }

    @Override
    public void render(final Batch batch) {
        fpsValue.set(Gdx.graphics.getFramesPerSecond());
        if (fpsValue.get() > 0) {
            Vector2 translate = transformComponent.getTranslate();
            formatInto(fpsLabel, FPS_LABEL_FORMAT, fpsValue);
            fpsLabel.flip();
            font.draw(batch, fpsLabel, translate.x, translate.y);
        }
    }
}
