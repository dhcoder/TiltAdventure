package dhcoder.tool.javafx.game.model;

import dhcoder.tool.javafx.property.PropertyDefn;

/**
 * Data class that corresponds to a TiltAdventure scene.
 */
@PropertyDefn(name = "NumRows", category = "Grid Size")
@PropertyDefn(name = "NumCols", displayName = "Num Columns", category = "Grid Size")
@PropertyDefn(name = "Tileset", exclude = true)
@PropertyDefn(name = "Width", category = "Size")
@PropertyDefn(name = "Height", category = "Size")
public final class Scene {
    private final Tileset tileset;
    private int numRows;
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

    public void setNumRows(int numRows) { this.numRows = numRows; }

    public int getNumCols() {
        return numCols;
    }

    public int getWidth() { return numCols * tileset.getTileWidth(); }
    public int getHeight() { return numRows * tileset.getTileHeight(); }
}
