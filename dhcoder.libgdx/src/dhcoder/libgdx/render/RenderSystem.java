package dhcoder.libgdx.render;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.utils.Array;

import java.util.Comparator;

/**
 * System that renders a bunch of renderable components.
 */
public final class RenderSystem {
    private static final Comparator<Renderable> RENDERABLE_COMPARATOR = new Comparator<Renderable>() {
        @Override
        public int compare(final Renderable r1, final Renderable r2) {
            return Float.compare(r1.getZ(), r2.getZ());
        }
    };

    private final Array<Renderable> renderables;

    public RenderSystem(final int capacity) {
        renderables = new Array<Renderable>(capacity);
    }

    /**
     * Add a renderable which will get drawn by {@link #render(Batch)}
     */
    public void add(final Renderable renderable) {
        renderables.add(renderable);
    }

    public void remove(final Renderable renderable) {
        renderables.removeValue(renderable, true);
    }

    /**
     * Render all renderables previously added by {@link #add(Renderable)}. When this method is done, the list
     * of render requests will be cleared, and must be added again for the next frame.
     */
    public void render(final Batch batch) {
        renderables.sort(RENDERABLE_COMPARATOR);
        int numSprites = renderables.size;
        for (int i = 0; i < numSprites; i++) {
            Renderable renderable = renderables.get(i);
            renderable.render(batch);
        }
    }
}