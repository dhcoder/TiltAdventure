package dhcoder.tool.javafx.game.model;

/**
 * Data class that corresponds to a TiltAdventure scene.
 */
public final class Scene {
    private final Tileset tileset;
    private final int numRows;
    private final int numCols;

    public Scene(final Tileset tileset, final int numRows, final int numCols) {
        this.tileset = tileset;

        this.numRows = numRows;
        this.numCols = numCols;
    }

    public Tileset getTileset() {
        return tileset;
    }

    public int getNumRows() {
        return numRows;
    }

    public int getNumCols() {
        return numCols;
    }
}
