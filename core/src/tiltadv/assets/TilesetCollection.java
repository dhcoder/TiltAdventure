package tiltadv.assets;

import dhcoder.support.collection.ArrayMap;

/**
 * A collection of all tilesets loaded so far for this game.
 */
public final class TilesetCollection {

    ArrayMap<String, Tileset> tilesets = new ArrayMap<String, Tileset>();

    public void add(final String path, final Tileset texture) {
        tilesets.put(path, texture);
    }

    public Tileset get(final String path) {
        return tilesets.get(path);
    }
}
