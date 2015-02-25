package dhcoder.tool.javafx.control;

import dhcoder.support.opt.Opt;
import dhcoder.tool.javafx.utils.ImageUtils;
import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;

import static dhcoder.support.text.StringUtils.format;

/**
 * JavaFX control for rendering an image overlaid with a grid, furthermore allowing you to select a tile or range of
 * tiles.
 */
public final class TileCanvas extends ResizableCanvas {

    public static final int DEFAULT_GRID_WIDTH = 0;
    public static final int DEFAULT_GRID_HEIGHT = 0;
    private final Opt<Image> resampledImageOpt = Opt.withNoValue();
    private final SimpleIntegerProperty tileWidth = new SimpleIntegerProperty(DEFAULT_GRID_WIDTH) {
        @Override
        protected void invalidated() {
            enqueueRefresh(false);
        }
    };
    private final SimpleIntegerProperty tileHeight = new SimpleIntegerProperty(DEFAULT_GRID_HEIGHT) {
        @Override
        protected void invalidated() {
            enqueueRefresh(false);
        }
    };
    private final SimpleIntegerProperty zoomFactor = new SimpleIntegerProperty(1) {
        @Override
        protected void invalidated() {
            enqueueRefresh(true);
        }
    };
    private final SimpleObjectProperty<Color> backgroundColor = new SimpleObjectProperty<Color>(Color.MAGENTA) {
        @Override
        protected void invalidated() {
            enqueueRefresh(false);
        }
    };
    private final TileCanvasSelectionModel selectionModel = new TileCanvasSelectionModel(this);
    private final SimpleObjectProperty<Image> image = new SimpleObjectProperty<Image>() {
        @Override
        protected void invalidated() {
            selectionModel.clearSelection();
            enqueueRefresh(true);
        }
    };
    private int xClick, yClick;
    private int xOver, yOver;

    private boolean imageInvalidated;
    private boolean refreshRequested;

    public TileCanvas(final double width, final double height) {
        super(width, height);
        selectionModel.getSelectedIndices().addListener((Observable observable) -> enqueueRefresh(false));

        initMouseOverLogic();
        initSelectionLogic();
    }

    public TileCanvas() {
        this(0, 0);
    }

    public Color getBackgroundColor() { return backgroundColor.getValue(); }

    public void setBackgroundColor(final Color value) { backgroundColor.set(value);}

    public SimpleObjectProperty<Color> backgroundColorProperty() { return backgroundColor; }

    public Opt<Image> getImageOpt() { return Opt.ofNullable(image.getValue()); }

    public void setImage(final Image value) { image.set(value); }

    public void clearImage() { image.set(null); }

    public SimpleObjectProperty<Image> imageProperty() { return image; }

    public int getTileHeight() { return tileHeight.getValue(); }

    public void setTileHeight(final int value) { tileHeight.set(value);}

    public SimpleIntegerProperty tileHeightProperty() { return tileHeight; }

    public int getTileWidth() { return tileWidth.getValue(); }

    public void setTileWidth(final int value) { tileWidth.set(value);}

    public SimpleIntegerProperty tileWidthProperty() { return tileWidth; }

    public int getZoomFactor() { return zoomFactor.getValue(); }

    public void setZoomFactor(final int value) { zoomFactor.set(value);}

    public SimpleIntegerProperty zoomFactorProperty() { return zoomFactor; }

    public TileCanvasSelectionModel getSelectionModel() { return selectionModel; }

    /**
     * Given a 1D index, get the 2D coordinate associated with it. The tiles count from left to right, then top to
     * bottom.
     *
     * @throws IllegalArgumentException if the index is < 0 or too large.
     */
    public Tile getTile(final int index) {
        int intWidth = (int)getWidth();
        int numHorizTiles = intWidth / getTileWidth();

        return new Tile(index % numHorizTiles, index / numHorizTiles);
    }

    public int getIndexAfter(final int index) {
        int maxIndex = assertInBounds(index);
        return Math.min(index + 1, maxIndex);
    }

    public int getIndexBefore(final int index) {
        assertInBounds(index);
        return Math.max(0, index - 1);
    }

    public int getLastIndex() {
        int intWidth = (int)getWidth();
        int numHorizTiles = intWidth / getTileWidth();

        int intHeight = (int)getHeight();
        int numVertTiles = intHeight / getTileHeight();

        return numHorizTiles * numVertTiles - 1;
    }

    public int getIndex(final Tile tile) {
        int intWidth = (int)getWidth();
        int numHorizTiles = intWidth / getTileWidth();

        return tile.getX() + tile.getY() * numHorizTiles;
    }

    private void initSelectionLogic() {
        setOnMouseClicked(event -> {
            int zoomFactor = getZoomFactor();
            int tileWidth = getTileWidth();
            int tileHeight = getTileHeight();
            xClick = ((int)event.getX() / zoomFactor) / tileWidth;
            yClick = ((int)event.getY() / zoomFactor) / tileHeight;

            Tile tile = new Tile(xClick, yClick);
            if (event.isShortcutDown()) {
                selectionModel.toggle(tile);
            }
            else if (event.isShiftDown()) {
                selectionModel.rangeSelect(tile);
            }
            else {
                selectionModel.clearAndSelect(tile);
            }
        });
        setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ESCAPE) {
                selectionModel.clearSelection();
            }
        });
    }

    private void initMouseOverLogic() {
        xOver = yOver = -1;
        setOnMouseMoved(event -> {
            int xOverLocal = -1;
            int yOverLocal = -1;
            int tileWidth = getTileWidth();
            int tileHeight = getTileHeight();
            if (tileWidth > 0 && tileHeight > 0) {
                int zoomFactor = getZoomFactor();
                xOverLocal = ((int)event.getX() / zoomFactor) / tileWidth;
                yOverLocal = ((int)event.getY() / zoomFactor) / tileHeight;
            }

            if (xOver != xOverLocal || yOver != yOverLocal) {
                xOver = xOverLocal;
                yOver = yOverLocal;
                enqueueRefresh(false);
            }
        });
        setOnMouseExited(event -> {
            xOver = yOver = -1;
            enqueueRefresh(false);
        });
    }

    /**
     * Ensure this index is in bounds, furthermore returning the max value this index could be set to.
     */
    private int assertInBounds(final int index) {
        int lastIndex = getLastIndex();

        if (index < 0 || index > lastIndex) {
            throw new IllegalArgumentException(format("Invalid index {0} is out of bounds [0,{1}]", index, lastIndex));
        }
        return lastIndex;
    }

    // Call enqueueRefresh instead of refreshing directly, so that multiple property changes in a row have a chance to
    // get merged into a single refresh call.
    private void enqueueRefresh(final boolean invalidateImage) {
        imageInvalidated = imageInvalidated || invalidateImage;
        if (!refreshRequested) {
            refreshRequested = true;
            Platform.runLater(this::refresh);
        }
    }

    @Override
    protected void refresh() {
        refreshRequested = false;
        if (imageInvalidated) {
            imageInvalidated = false;
            Image imageValue = image.getValue();
            if (imageValue != null) {
                resampledImageOpt.set(ImageUtils.zoom(imageValue, getZoomFactor()));
                setHeight((int)imageValue.getHeight() * getZoomFactor());
                setWidth((int)imageValue.getWidth() * getZoomFactor());
            }
            else {
                resampledImageOpt.clear();
            }
        }

        GraphicsContext g = getGraphicsContext2D();
        g.setFill(backgroundColor.getValue());
        g.fillRect(0, 0, getWidth(), getHeight());
        if (!resampledImageOpt.hasValue()) {
            return;
        }

        Image image = resampledImageOpt.getValue();
        g.drawImage(image, 0, 0, getWidth(), getHeight());

        g.setStroke(Color.BLACK);
        g.setLineWidth(zoomFactor.doubleValue() / 2d);

        int tileWidthZoomed = getTileWidth() * getZoomFactor();
        int tileHeightZoomed = getTileHeight() * getZoomFactor();
        if (tileWidthZoomed > 0 && tileHeightZoomed > 0) {
            for (int x = 0; x <= getWidth(); x += tileWidthZoomed) {
                g.strokeLine(x, 0, x, getHeight());
            }
            for (int y = 0; y <= getHeight(); y += tileHeightZoomed) {
                g.strokeLine(0, y, getWidth(), y);
            }

            if (xOver != -1 && yOver != -1) {
                g.setFill(Color.grayRgb(255, 0.5));
                g.fillRect(xOver * tileWidthZoomed, yOver * tileHeightZoomed, tileWidthZoomed, tileHeightZoomed);
            }

            for (Tile tile : selectionModel.getSelectedItems()) {
                int x = tile.getX() * tileWidthZoomed;
                int y = tile.getY() * tileHeightZoomed;
                g.setFill(Color.rgb(255, 255, 0, selectionModel.isAnchor(tile) ? 0.5 : 0.25));
                g.fillRect(x, y, tileWidthZoomed, tileHeightZoomed);
                g.setStroke(Color.WHITE);
                g.strokeRect(x, y, tileWidthZoomed, tileHeightZoomed);
            }
        }

    }
}