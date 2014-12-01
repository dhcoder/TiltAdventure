package tiltadv.tools.scene;

import dhcoder.libgdx.assets.Scene;
import dhcoder.tool.history.History;

/**
 * Miscellaneous context data related to a scene. Toolbars in this tool should be able to repond to a context change and
 * update their UI.
 */
public final class SceneContext {
    private final History history = new History();
    private final Scene scene;

    public SceneContext() {
        scene = null;
    }
}
