package tiltadv.components.sprite;

import com.badlogic.gdx.graphics.g2d.Sprite;
import tiltadv.entity.AbstractComponent;
import tiltadv.entity.Entity;

/**
 * A component that encapsulates the logic of rendering a sprite.
 */
public class SpriteComponent extends AbstractComponent {

    private Sprite sprite;

    public SpriteComponent() {
    }

    public SpriteComponent(final Sprite sprite) {
        this.sprite = sprite;
    }

    @Override
    public void initialize(final Entity owner) {
        super.initialize(owner);
    }

    public Sprite getSprite() {
        return sprite;
    }

    public void setSprite(final Sprite sprite) {
        this.sprite = sprite;
    }
}
