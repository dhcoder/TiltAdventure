package dhcoder.tool.javafx.control;

import static dhcoder.support.text.StringUtils.format;

/**
* TODO: HEADER COMMENT HERE.
*/
public class Tile {
    private final int x;
    private final int y;

    public Tile(final int x, final int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    @Override
    public int hashCode() {
        int result = x;
        result = 31 * result + y;
        return result;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }

        Tile tile = (Tile)o;

        if (x != tile.x) { return false; }
        if (y != tile.y) { return false; }

        return true;
    }

    @Override
    public String toString() {
        return format("Grid[{0}][{1}]", x, y);
    }
}
