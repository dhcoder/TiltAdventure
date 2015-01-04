package dhcoder.tool.game.view;

import dhcoder.tool.game.model.Tile;
import dhcoder.tool.game.model.Tileset;
import javafx.geometry.Rectangle2D;
import javafx.scene.image.ImageView;
import org.controlsfx.control.GridCell;
import org.controlsfx.control.GridView;

/**
 * A {@link GridCell} that helps display tiles in a tileset, for use with {@link GridView}
 */
public final class TileGridCell extends GridCell<Tile> {
    private final ImageView tileView;

    public TileGridCell() {
        tileView = new ImageView();
        tileView.fitWidthProperty().bind(widthProperty());
        tileView.fitHeightProperty().bind(heightProperty());
    }

    @Override
    protected void updateItem(final Tile tile, final boolean empty) {
        super.updateItem(tile, empty);

        if (empty) {
            tileView.setImage(null);
            setGraphic(null);
            return;
        }

        Tileset tileset = tile.getTileset();
        tileView.setImage(tileset.getImage());
        tileView.setViewport(
            new Rectangle2D(tile.getCoordX() * tileset.getTileWidth(), tile.getCoordY() * tileset.getTileHeight(),
                tileset.getTileWidth(), tileset.getTileHeight()));
        setGraphic(tileView);
    }
}

