package dhcoder.tool.javafx.game.model;

import javafx.scene.image.Image;

import java.io.File;

/**
 * Data class that corresponds to a TiltAdventure tileset.
 */
public final class Tileset {
    private final Image image;
    private final int tileWidth;
    private final int tileHeight;

    public Tileset(final File imageFile, final int tileWidth, final int tileHeight) {
        this.image = new Image("file:" + imageFile.toString());
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
