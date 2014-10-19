package tiltadv.globals;

import dhcoder.libgdx.render.RenderSystem;

/**
 * Collection of all render layers in this application.
 */
public final class RenderLayers {
    private final RenderSystem gameRenderLayer;
    private final RenderSystem uiRenderLayer;

    public RenderSystem getGameRenderLayer() {
        return gameRenderLayer;
    }

    public RenderSystem getUiRenderLayer() {
        return uiRenderLayer;
    }

    public RenderLayers(final RenderSystem gameRenderLayer, final RenderSystem uiRenderLayer) {
        this.gameRenderLayer = gameRenderLayer;
        this.uiRenderLayer = uiRenderLayer;
    }
}
