package dhcoder.tool.javafx.control;

import dhcoder.support.opt.Opt;
import dhcoder.tool.javafx.utils.ImageUtils;
import javafx.application.Platform;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

/**
 * JavaFX control for rendering an image overlaid with a grid, furthermore allowing you to select a tile or range of
 * tiles.
 */
public final class TiledImageView {

    public static final int DEFAULT_TILE_WIDTH = 16;
    public static final int DEFAULT_TILE_HEIGHT = 16;
    private final ResizableCanvas canvas = new ResizableCanvas();
    private final Opt<Image> resampledImageOpt = Opt.withNoValue();

    private final SimpleObjectProperty<Image> image = new SimpleObjectProperty<Image>() {
        @Override
        protected void invalidated() {
            if (image.getValue() == null) {
                resampledImageOpt.clear();
            }
            enqueueRefresh(true);
        }
    };

    private final SimpleIntegerProperty tileWidth = new SimpleIntegerProperty(DEFAULT_TILE_WIDTH) {
        @Override
        protected void invalidated() {
            enqueueRefresh(true);
        }
    };

    private final SimpleIntegerProperty tileHeight = new SimpleIntegerProperty(DEFAULT_TILE_HEIGHT) {
        @Override
        protected void invalidated() {
            enqueueRefresh(true);
        }
    };

    private final SimpleIntegerProperty zoomFactor = new SimpleIntegerProperty(1) {
        @Override
        protected void invalidated() {
            enqueueRefresh(true);
        }
    };

    private boolean refreshRequested;

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

    private void enqueueRefresh(final boolean invalidateImage) {
        if (!refreshRequested) {
            refreshRequested = true;
            Platform.runLater(this::refresh);
        }
    }

    private void refresh() {
        refreshRequested = false;
        Image imageValue = image.getValue();
        if (imageValue != null) {
            resampledImageOpt.set(ImageUtils.zoom(imageValue, getZoomFactor()));
            canvas.setHeight((int)imageValue.getHeight() * getZoomFactor());
            canvas.setWidth((int)imageValue.getWidth() * getZoomFactor());
        }
        else {
            resampledImageOpt.clear();
        }

        GraphicsContext g = canvas.getGraphicsContext2D();
        if (!resampledImageOpt.hasValue()) {
            g.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
            return;
        }

        Image image = resampledImageOpt.getValue();
        g.setFill(Color.WHITE);
        g.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
        g.drawImage(image, 0, 0, canvas.getWidth(), canvas.getHeight());

        g.setStroke(Color.BLACK);
        g.setLineWidth(1);
        int tileWidthZoomed = getTileWidth() * getZoomFactor();
        int tileHeightZoomed = getTileHeight() * getZoomFactor();
        for (int x = 0; x <= canvas.getWidth(); x += tileWidthZoomed) {
            g.strokeLine(x, 0, x, canvas.getHeight());
        }
        for (int y = 0; y <= canvas.getHeight(); y += tileHeightZoomed) {
            g.strokeLine(0, y, canvas.getWidth(), y);
        }
    }
}
