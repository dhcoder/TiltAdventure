package tiltadv.data;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import dhcoder.support.time.Duration;
import tiltadv.assets.Tile;

/**
* A tile that animates over time
*/
public final class DynamicTile implements Tile {

    private final Animation animation;
    private final Duration elapsedSoFar = Duration.zero();

    public DynamicTile(final Animation animation) {
        this.animation = animation;
    }

    @Override
    public void update(final Duration duration) {
        elapsedSoFar.add(duration);
        if (duration.getSeconds() >= animation.getAnimationDuration()) {
            duration.subtractSeconds(animation.getAnimationDuration());
        }
    }

    @Override
    public TextureRegion getImage() {
        return animation.getKeyFrame(elapsedSoFar.getSeconds());
    }
}

