package tiltadv.scene;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import dhcoder.support.time.Duration;

/**
 * A tile that never changes its image
 */
public final class StaticTile implements Tile {

    private TextureRegion image;

    public StaticTile(final TextureRegion image) {
        this.image = image;
    }

    @Override
    public void update(final Duration duration) {
        // Do nothing
    }

    @Override
    public TextureRegion getImage() {
        return image;
    }
}
