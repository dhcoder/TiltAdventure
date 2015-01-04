package dhcoder.tool.javafx.utils;

import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;

/**
 * Utility methods for {@link Image}s.
 */
public final class ImageUtils {

    public static Image zoom(final Image image, final int factor) {
        if (factor == 1) {
            return image;
        }

        if (factor <= 0) {
            throw new IllegalArgumentException("Can't zoom smaller than 2x");
        }

        int zoomWidth = (int)(image.getWidth() * factor);
        int zoomHeight = (int)(image.getHeight() * factor);
        WritableImage zoomedImage = new WritableImage(zoomWidth, zoomHeight);

        for (int dx = 0; dx < zoomWidth; ++dx) {
            for (int dy = 0; dy < zoomHeight; ++dy) {
                zoomedImage.getPixelWriter().setArgb(dx, dy, image.getPixelReader().getArgb(dx/factor, dy/factor));
            }
        }

        return zoomedImage;
    }
}
