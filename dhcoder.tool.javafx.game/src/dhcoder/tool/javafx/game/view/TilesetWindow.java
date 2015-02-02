package dhcoder.tool.javafx.game.view;

import dhcoder.tool.javafx.game.model.Tileset;
import dhcoder.tool.javafx.utils.FxController;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * A floating window which displays a tileset and lets the user select an active tile.
 */
public final class TilesetWindow extends Stage {

    private final TilesetWindowController controller;

    public TilesetWindow() {
        super(StageStyle.UTILITY);
        setTitle("Tileset Window");

        controller = FxController.loadView(TilesetWindowController.class);
        setScene(new Scene(controller.getRoot()));
    }

    public TilesetWindow setTileset(final Tileset tileset) {
        controller.tilesetCanvas.setImage(tileset.getImage());
        controller.tilesetCanvas.setTileWidth(tileset.getTileWidth());
        controller.tilesetCanvas.setTileHeight(tileset.getTileHeight());
        return this;
    }

    public TilesetWindow clear() {
        controller.tilesetCanvas.clearImage();
        return this;
    }
}
