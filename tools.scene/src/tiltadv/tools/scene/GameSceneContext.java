package tiltadv.tools.scene;

import dhcoder.tool.game.model.Scene;
import dhcoder.tool.history.History;

/**
 * Miscellaneous context data related to a scene. Toolbars in this tool should be able to repond to a context change and
 * update their UI.
 */
public final class GameSceneContext {
    private final History history = new History();
    private final Scene scene;

    public GameSceneContext(final Scene scene) {
        this.scene = scene;
    }

    public History getHistory() {
        return history;
    }

    public Scene getScene() {
        return scene;
    }
}
