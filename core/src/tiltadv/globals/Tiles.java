package tiltadv.globals;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import tiltadv.assets.TileDatastore;

/**
 * Static class which holds all tiles used by our game.
 */
public final class Tiles {
    public static final TextureRegion BOULDER;
    public static final TextureRegion ROCK;
    public static final TextureRegion SWORDRIGHT;
    public static final TextureRegion SENSOR;

    public static final TextureRegion GRASS;
    public static final TextureRegion GRASS_ROCK;
    public static final TextureRegion GRASS_FLOWER;

    static {
        final TileDatastore tiles = Services.get(TileDatastore.class);

        BOULDER = tiles.get("zelda", "boulder");
        ROCK = tiles.get("zelda", "rock");
        SWORDRIGHT = tiles.get("zelda", "sword");
        SENSOR = tiles.get("zelda", "player_sensor");

        GRASS = tiles.get("grass", "plain");
        GRASS_ROCK = tiles.get("grass", "rock");
        GRASS_FLOWER = tiles.get("grass", "flower");
    }

    private Tiles() { } // Disabled constructor
}
