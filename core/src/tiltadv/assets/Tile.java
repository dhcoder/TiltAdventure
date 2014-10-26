package tiltadv.assets;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import dhcoder.support.time.Duration;

/**
* A tile is a square image meant to render the ground.
*/
public interface Tile {
    void update(Duration duration);
    TextureRegion getImage();
}
