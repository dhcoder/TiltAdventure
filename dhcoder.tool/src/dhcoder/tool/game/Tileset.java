package dhcoder.tool.game;

import javafx.scene.image.Image;

import java.nio.file.Path;

/**
 * Data class that corresponds to a TiltAdventure tileset.
 */
public final class Tileset {
    private final Image image;
    private final int tileWidth;
    private final int tileHeight;
    private final int numRows;
    private final int numCols;

    public Tileset(final Path path, final int tileWidth, final int tileHeight, final int numRows, final int numCols) {
        this.image = new Image(path.toString());
        this.tileWidth = tileWidth;
        this.tileHeight = tileHeight;
        this.numRows = numRows;
        this.numCols = numCols;
    }
}
