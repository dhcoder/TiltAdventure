package tiltadv.tools.scene.view;

import dhcoder.tool.javafx.utils.FxController;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * A floating window which displays a tileset and lets the user select an active tile.
 */
public final class SceneEditor extends Stage {

    private final SceneEditorController controller;

    public SceneEditor() {
        super(StageStyle.UTILITY);
        setTitle("Tileset Window");

        controller = FxController.loadView(SceneEditorController.class);
        setScene(new Scene(controller.getRoot()));
    }

    public SceneEditor setGameScene(final dhcoder.tool.javafx.game.model.Scene gameScene) {
//        controller.tilesetCanvas.setImage(tileset.getImage());
//        controller.tilesetCanvas.setTileWidth(tileset.getTileWidth());
//        controller.tilesetCanvas.setTileHeight(tileset.getTileHeight());
        return this;
    }

    public SceneEditor clear() {
        controller.tilesetCanvas.clearImage();
        return this;
    }
}
