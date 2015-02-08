package dhcoder.tool.javafx.control;

import javafx.beans.InvalidationListener;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;

import java.util.function.Consumer;

/**
 * A canvas which can be resized dynamically.
 * <p/>
 * See also <a href=https://dlemmermann.wordpress.com/2014/04/10/javafx-tip-1-resizable-canvas/>Resizable Canvas tip</a>
 */
public class ResizableCanvas extends Canvas {

    private Consumer<ResizableCanvas> onResized;

    public ResizableCanvas() { this(0, 0); }

    public ResizableCanvas(final double width, final double height) {
        super(width, height);

        // Redraw canvas when size changes.
        InvalidationListener sizeListener = evt -> {
            clear();
            refresh();

            if (onResized != null) {
                onResized.accept(this);
            }
        };

        widthProperty().addListener(sizeListener);
        heightProperty().addListener(sizeListener);
    }

    @Override
    public boolean isResizable() {
        return true;
    }

    @Override
    public double minWidth(final double height) {
        return 0d;
    }

    @Override
    public double minHeight(final double width) {
        return 0d;
    }

    @Override
    public double prefWidth(final double height) {
        return getWidth();
    }

    @Override
    public double prefHeight(final double width) {
        return getHeight();
    }

    @Override
    public double maxWidth(final double height) {
        return Double.MAX_VALUE;
    }

    @Override
    public double maxHeight(final double width) {
        return Double.MAX_VALUE;
    }

    @Override
    public void resize(final double width, final double height) {
        setWidth(width);
        setHeight(height);
    }

    public GraphicsContext clear() {
        double width = getWidth();
        double height = getHeight();

        GraphicsContext gc = getGraphicsContext2D();
        gc.clearRect(0, 0, width, height);

        return gc;
    }

    public void setOnResized(final Consumer<ResizableCanvas> onResized) {
        this.onResized = onResized;
    }

    protected void refresh() {}
}