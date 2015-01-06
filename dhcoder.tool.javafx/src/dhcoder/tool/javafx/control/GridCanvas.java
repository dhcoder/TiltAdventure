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
public final class GridCanvas extends ResizableCanvas {

    public static final int DEFAULT_GRID_WIDTH = 16;
    public static final int DEFAULT_GRID_HEIGHT = 16;
    private final Opt<Image> resampledImageOpt = Opt.withNoValue();

    private final SimpleObjectProperty<Image> image = new SimpleObjectProperty<Image>() {
        @Override
        protected void invalidated() {
            enqueueRefresh(true);
        }
    };

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

    public Color getBackgroundColor() { return backgroundColor.getValue(); }

    public void setBackgroundColor(final Color value) { backgroundColor.set(value);}

    public SimpleObjectProperty<Color> backgroundColorProperty() { return backgroundColor; }

    private boolean imageInvalidated;
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

    // Call enqueueRefresh instead of refreshing directly, so that multiple property changes in a row have a chance to
    // get merged into a single refresh call.
    private void enqueueRefresh(final boolean invalidateImage) {
        imageInvalidated = imageInvalidated || invalidateImage;
        if (!refreshRequested) {
            refreshRequested = true;
            Platform.runLater(this::refresh);
        }
    }

    private void refresh() {
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
        if (!resampledImageOpt.hasValue()) {
            g.clearRect(0, 0, getWidth(), getHeight());
            return;
        }

        Image image = resampledImageOpt.getValue();
        g.setFill(backgroundColor.getValue());
        g.fillRect(0, 0, getWidth(), getHeight());
        g.drawImage(image, 0, 0, getWidth(), getHeight());

        g.setStroke(Color.BLACK);
        g.setLineWidth(1);
        int tileWidthZoomed = getTileWidth() * getZoomFactor();
        int tileHeightZoomed = getTileHeight() * getZoomFactor();
        for (int x = 0; x <= getWidth(); x += tileWidthZoomed) {
            g.strokeLine(x, 0, x, getHeight());
        }
        for (int y = 0; y <= getHeight(); y += tileHeightZoomed) {
            g.strokeLine(0, y, getWidth(), y);
        }
    }
}
