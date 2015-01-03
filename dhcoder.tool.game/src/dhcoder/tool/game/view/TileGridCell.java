package dhcoder.tool.game.view;

import javafx.scene.image.ImageView;
import org.controlsfx.control.GridCell;
import org.controlsfx.control.GridView;

/**
 * A {@link GridCell} that helps display tiles in a tileset, for use with {@link GridView}
 */
public final class TileGridCell extends GridCell<ImageView> {
    // Todo, take in a Tile, convert to an ImageView...

    @Override
    protected void updateItem(final ImageView imageView, final boolean empty) {
        super.updateItem(imageView, empty);
        imageView.setFitWidth(getWidth());
        imageView.setFitHeight(getHeight());
        setGraphic(empty ? null : imageView);
    }
}

