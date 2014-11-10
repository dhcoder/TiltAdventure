package tiltadv.globals;

import dhcoder.libgdx.assets.AnimationDatastore;
import dhcoder.libgdx.assets.ImageDatastore;
import dhcoder.libgdx.assets.SceneDatastore;
import dhcoder.libgdx.assets.TileDatastore;
import dhcoder.libgdx.assets.TilesetDatastore;

/**
 * Collection of all game data loaded from disk.
 */
public final class GameData {
    public static ImageDatastore images = new ImageDatastore();
    public static TilesetDatastore tilesets = new TilesetDatastore();
    public static TileDatastore tiles = new TileDatastore();
    public static AnimationDatastore animations = new AnimationDatastore();
    public static SceneDatastore scenes = new SceneDatastore();
}
