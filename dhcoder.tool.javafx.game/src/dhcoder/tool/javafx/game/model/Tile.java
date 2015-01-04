package dhcoder.tool.javafx.game.model;

/**
 * Represents a single (x,y) tile in a tileset.
 */
public final class Tile {
    private final Tileset tileset;
    private final int coordX;
    private final int coordY;

    public Tile(final Tileset tileset, final int coordX, final int coordY) {
        this.tileset = tileset;
        this.coordX = coordX;
        this.coordY = coordY;
    }

    public Tileset getTileset() {
        return tileset;
    }

    public int getCoordX() {
        return coordX;
    }

    public int getCoordY() {
        return coordY;
    }
}
