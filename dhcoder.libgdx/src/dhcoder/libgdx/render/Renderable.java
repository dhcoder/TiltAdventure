package dhcoder.libgdx.render;

import com.badlogic.gdx.graphics.g2d.Batch;

/**
 * A tag for any class that can render itself via a {@link Batch}.
 */
public interface Renderable {
    /**
     * Renders this component, via a {@link Batch}.
     */
    void render(Batch batch);
}