package tiltadv.immutable;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import dhcoder.support.immutable.Immutable;

/**
 * A class that wraps a Sprite, offering read-only access to it.
 */
public class ImmutableSprite extends Immutable<Sprite> {

    public ImmutableSprite(final Sprite sprite) {
        super(sprite);
    }

    @Override
    public Sprite toMutable() {
        return new Sprite(wrappedMutable);
    }

    @Override
    public void copyInto(final Sprite target) {
        target.set(wrappedMutable);
    }

    public float getRotation() { return wrappedMutable.getRotation(); }

    public void draw(final Batch batch) { wrappedMutable.draw(batch); }

    public void draw(final Batch batch, final float alphaModulation) { wrappedMutable.draw(batch, alphaModulation); }

    public float getX() { return wrappedMutable.getX(); }

    public float getY() { return wrappedMutable.getY(); }

    public float getWidth() { return wrappedMutable.getWidth(); }

    public float getHeight() { return wrappedMutable.getHeight(); }

    public float getOriginX() { return wrappedMutable.getOriginX(); }

    public float getOriginY() { return wrappedMutable.getOriginY(); }

    public float getScaleX() { return wrappedMutable.getScaleX(); }

    public float getScaleY() { return wrappedMutable.getScaleY(); }

    public Color getColor() { return wrappedMutable.getColor(); }
}
