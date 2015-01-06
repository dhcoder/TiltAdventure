package dhcoder.tool.javafx.game.view;

import dhcoder.tool.javafx.control.TiledCanvas;
import dhcoder.tool.javafx.game.model.Tileset;
import dhcoder.tool.javafx.utils.FxController;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.scene.Scene;
import javafx.scene.control.Toggle;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * A floating window which displays a tileset and lets the user select an active tile.
 */
public final class TilesetWindow extends Stage {

    private static final int DEFAULT_HEIGHT = 300;
    private static final int DEFAULT_WIDTH = 300;

    private final TiledCanvas tilesetView = new TiledCanvas();

    public TilesetWindow() {
        super(StageStyle.UTILITY);
        setTitle("Tileset Window");

        final TilesetWindowController controller = FxController.loadView(TilesetWindowController.class);
        controller.contentPane.setContent(tilesetView);

        tilesetView.setZoomFactor(getZoomFactor(controller.zoomGroup.getSelectedToggle()));
        controller.zoomGroup.selectedToggleProperty().addListener(new InvalidationListener() {
            @Override
            public void invalidated(final Observable observable) {
                Toggle toggle = controller.zoomGroup.getSelectedToggle();
                tilesetView.setZoomFactor(getZoomFactor(toggle));
            }
        });

        setScene(new Scene(controller.getRoot(), DEFAULT_WIDTH, DEFAULT_HEIGHT));
    }


    public TilesetWindow setTileset(final Tileset tileset) {
        tilesetView.setImage(tileset.getImage());
        tilesetView.setTileWidth(tileset.getTileWidth());
        tilesetView.setTileHeight(tileset.getTileHeight());
        return this;
    }

    public TilesetWindow clearTileset() {
        tilesetView.clearImage();
        return this;
    }

    private int getZoomFactor(final Toggle toggle) {
        // UserData specified in FXML
        return Integer.parseInt((String)toggle.getUserData());
    }
}
