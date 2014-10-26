package tiltadv.assets;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import static dhcoder.support.text.StringUtils.format;

/**
 * Data that describes a scene, which is essentially a self-contained area full of ground tiles and entities.
 */
public final class Scene {

    public static boolean RUN_SANITY_CHECKS;

    // TODO: Allow multiple tilesets and animated tiles
    private final Tileset tileset;
    private final Array<TextureRegion> groundTiles;
    private final int sceneTileWidth;
    private final int sceneTileHeight;
    private final Vector2 center = new Vector2();

    public Scene(final Tileset tileset, final int sceneTileWidth, final int sceneTileHeight, final float centerX,
        final float centerY) {
        this.tileset = tileset;
        this.sceneTileWidth = sceneTileWidth;
        this.sceneTileHeight = sceneTileHeight;
        center.x = centerX;
        center.y = centerY;

        int numRegions = sceneTileWidth * sceneTileHeight;
        groundTiles = new Array<TextureRegion>(numRegions);
        for (int i = 0; i < numRegions; ++i) {
            groundTiles.add(null);
        }
    }

    public void setTile(final int tileX, final int tileY, final TextureRegion tile) {
        if (tileX >= sceneTileWidth || tileY >= sceneTileHeight) {
            throw new IllegalArgumentException(
                format("Invalid tile coordinates {0}x{1} (scene is {2}x{3})", tileX, tileY, sceneTileWidth,
                    sceneTileHeight));
        }

        int index = tileY * sceneTileWidth + tileX;
        groundTiles.set(index, tile);
    }

    public void setTile(final int tileIndex, final TextureRegion tile) {
        int tileX = tileIndex % sceneTileWidth;
        int tileY = tileIndex / sceneTileHeight;
        setTile(tileX, tileY, tile);
    }

}
