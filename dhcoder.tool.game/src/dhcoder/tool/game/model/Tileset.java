package dhcoder.tool.game.model;

import javafx.scene.image.Image;

import java.nio.file.Path;

/**
 * Data class that corresponds to a TiltAdventure tileset.
 */
public final class Tileset {
    private final Image image;
    private final int tileWidth;
    private final int tileHeight;

    public Tileset(final Path imagePath, final int tileWidth, final int tileHeight) {
        this.image = new Image(imagePath.toString());
        this.tileWidth = tileWidth;
        this.tileHeight = tileHeight;
    }

    public Image getImage() {
        return image;
    }

    public int getTileWidth() {
        return tileWidth;
    }

    public int getTileHeight() {
        return tileHeight;
    }
}
