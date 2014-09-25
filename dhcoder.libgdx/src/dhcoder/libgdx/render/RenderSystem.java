package dhcoder.libgdx.render;

import com.badlogic.gdx.graphics.g2d.Batch;

import java.util.ArrayList;

/**
 * System that renders a bunch of renderable components.
 */
public final class RenderSystem {
    private final ArrayList<Renderable> renderables;

    public RenderSystem(final int capacity) {
        renderables = new ArrayList<Renderable>(capacity);
    }

    /**
     * Add a renderable which will get drawn by {@link #render(Batch)}
     */
    public void add(final Renderable renderable) {
        renderables.add(renderable);
    }

    public void remove(final Renderable renderable) {
        renderables.remove(renderable);
    }

    /**
     * Render all renderables previously added by {@link #add(Renderable)}. When this method is done, the list
     * of render requests will be cleared, and must be added again for the next frame.
     */
    public void render(final Batch batch) {
        int numSprites = renderables.size();
        for (int i = 0; i < numSprites; i++) {
            Renderable renderable = renderables.get(i);
            renderable.render(batch);
        }
    }
}