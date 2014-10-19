package dhcoder.libgdx.render;

import com.badlogic.gdx.graphics.OrthographicCamera;
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
    private final OrthographicCamera camera;
    private final Array<Renderable> renderables;

    public RenderSystem(final float viewportWidth, final float viewportHeight, final int capacity) {
        camera = new OrthographicCamera(viewportWidth, viewportHeight);
        renderables = new Array<Renderable>(capacity);
    }

    public OrthographicCamera getCamera() {
        return camera;
    }

    /**
     * Add a renderable which will get drawn by {@link #render(Batch)}
     */
    public void add(final Renderable renderable) {
        renderables.add(renderable);
    }

    /**
     * Remove a renderable added by {@link #add(Renderable)}
     */
    public void remove(final Renderable renderable) {
        renderables.removeValue(renderable, true);
    }

    /**
     * Update this system - this should be called before calling render.
     */
    public void update(final Batch batch) {
        camera.update();
        batch.setProjectionMatrix(camera.combined);
    }

    /**
     * Render all renderables previously added by {@link #add(Renderable)}. When this method is done, the list
     * of render requests will be cleared, and must be added again for the next frame.
     */
    public void render(final Batch batch) {
        int numSprites = renderables.size;
        if (numSprites == 0) {
            return;
        }

        batch.begin();

        renderables.sort(RENDERABLE_COMPARATOR);
        for (int i = 0; i < numSprites; i++) {
            Renderable renderable = renderables.get(i);
            renderable.render(batch);
        }

        batch.end();
    }
}