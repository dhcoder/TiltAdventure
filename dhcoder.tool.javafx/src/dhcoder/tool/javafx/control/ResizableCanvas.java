package dhcoder.tool.javafx.control;

import javafx.beans.InvalidationListener;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;

/**
 * A canvas which can be resized dynamically.
 * <p/>
 * See also <a href=https://dlemmermann.wordpress.com/2014/04/10/javafx-tip-1-resizable-canvas/>Resizable Canvas tip</a>
 */
public final class ResizableCanvas extends Canvas {

    public ResizableCanvas() { this(0, 0); }

    public ResizableCanvas(final double width, final double height) {
        super(width, height);
        // Redraw canvas when size changes.
        InvalidationListener sizeListener = evt -> draw();

        widthProperty().addListener(sizeListener);
        heightProperty().addListener(sizeListener);
    }

    @Override
    public boolean isResizable() {
        return true;
    }

    @Override
    public double prefWidth(final double height) {
        return getWidth();
    }

    @Override
    public double prefHeight(final double width) {
        return getHeight();
    }

    private void draw() {
        double width = getWidth();
        double height = getHeight();

        GraphicsContext gc = getGraphicsContext2D();
        gc.clearRect(0, 0, width, height);
    }
}