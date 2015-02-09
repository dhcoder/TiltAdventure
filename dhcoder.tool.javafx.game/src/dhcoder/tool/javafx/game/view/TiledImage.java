package dhcoder.tool.javafx.game.view;

import com.google.common.collect.Lists;
import dhcoder.tool.javafx.control.Tile;
import dhcoder.tool.javafx.game.model.Tileset;
import javafx.application.Platform;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

import java.util.List;
import java.util.function.Consumer;

public final class TiledImage extends WritableImage {

    private static List<Tile> generateDefaultPalette(final Tileset tileset) {
        List<Tile> palette = Lists.newArrayListWithCapacity(tileset.getNumCols() * tileset.getNumRows());
        for (int x = 0; x < tileset.getNumCols(); x++) {
            for (int y = 0; y < tileset.getNumRows(); y++) {
                palette.add(new Tile(x, y));
            }
        }

        return palette;
    }

    private final SimpleObjectProperty<Color> backgroundColor = new SimpleObjectProperty<Color>(Color.TRANSPARENT) {
        @Override
        protected void invalidated() {
            enqueueRefresh();
        }
    };
    private final SimpleListProperty<Tile> tilePalette =
        new SimpleListProperty<Tile>(FXCollections.observableArrayList()) {
            @Override
            protected void invalidated() {
                enqueueRefresh();
            }
        };
    private final SimpleListProperty<Integer> tileIndices =
        new SimpleListProperty<Integer>(FXCollections.observableArrayList()) {
            @Override
            protected void invalidated() {
                enqueueRefresh();
            }
        };
    private final Tileset tileset;
    private Consumer<TiledImage> onRefreshed;
    private boolean refreshRequested;

    public TiledImage(final Tileset tileset, final int numRows, final int numCols) {
        this(tileset, numRows, numCols, generateDefaultPalette(tileset));
    }

    public TiledImage(final Tileset tileset, final int numRows, final int numCols, final List<Tile> tilePalette) {
        super(numCols * tileset.getTileWidth(), numRows * tileset.getTileHeight());
        this.tileset = tileset;
        this.tilePalette.get().setAll(tilePalette);
    }

    public ObservableList<Tile> getTilePalette() { return tilePalette.getValue(); }

    public SimpleListProperty<Tile> tilePaletteProperty() { return tilePalette; }

    public Color getBackgroundColor() { return backgroundColor.getValue(); }

    public void setBackgroundColor(final Color value) { backgroundColor.set(value);}

    public SimpleObjectProperty<Color> backgroundColorProperty() { return backgroundColor; }

    public ObservableList<Integer> getTileIndices() { return tileIndices.getValue(); }

    public SimpleListProperty<Integer> tileIndicesProperty() { return tileIndices; }

    public void setOnRefreshed(final Consumer<TiledImage> onRefreshed) {
        this.onRefreshed = onRefreshed;
    }

    private void enqueueRefresh() {
        if (refreshRequested) {
            return;
        }

        refreshRequested = true;
        Platform.runLater(() -> {
            refresh();
            fireOnRefreshed();
        });
    }

    private void refresh() {
        refreshRequested = false;

        final PixelWriter writer = getPixelWriter();

        for (int x = 0; x < getWidth(); x++) {
            for (int y = 0; y < getHeight(); y++) {
                writer.setColor(x, y, backgroundColor.getValue());
            }
        }

        if (tileIndices.isEmpty()) {
            return;
        }

        final PixelReader reader = tileset.getImage().getPixelReader();
        final int tileWidth = tileset.getTileWidth();
        final int tileHeight = tileset.getTileHeight();

        int xDest = 0, yDest = 0;
        for (Integer tileIndex : tileIndices.getValue()) {
            if (tileIndex >= tilePalette.size()) {
                continue;
            }

            Tile tile = tilePalette.get(tileIndex);
            int xSrc = tile.getX() * tileWidth;
            int ySrc = tile.getY() * tileHeight;

            for (int x = 0; x < tileWidth; x++) {
                for (int y = 0; y < tileHeight; y++) {
                    writer.setArgb(xDest + x, yDest + y, reader.getArgb(xSrc + x, ySrc + y));
                }
            }

            xDest += tileWidth;
            if (xDest + tileWidth >= getWidth()) {
                xDest = 0;
                yDest += tileHeight;
                if (yDest + tileHeight >= getHeight()) {
                    break;
                }
            }
        }
    }

    private void fireOnRefreshed() {
        if (onRefreshed != null) {
            onRefreshed.accept(this);
        }
    }
}
