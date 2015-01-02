package tiltadv.tools.scene;

import dhcoder.tool.history.History;
import tiltadv.tools.scene.model.GameScene;

/**
 * Miscellaneous context data related to a scene. Toolbars in this tool should be able to repond to a context change and
 * update their UI.
 */
public final class GameSceneContext {
    private final History history = new History();
    private final GameScene scene;

    public GameSceneContext(final GameScene scene) {
        this.scene = scene;
    }

    public History getHistory() {
        return history;
    }

    public GameScene getScene() {
        return scene;
    }
}
