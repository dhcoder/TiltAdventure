package tiltadv.serialization;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Json;
import tiltadv.assets.Scene;
import tiltadv.assets.SceneDatastore;
import tiltadv.assets.Tileset;
import tiltadv.assets.TilesetDatastore;
import tiltadv.globals.Services;

import static dhcoder.support.text.StringUtils.format;

/**
 * Class that loads {@link Scene}s into our {@link SceneDatastore}.
 */
public final class SceneLoader {

    private final static class SceneData {
        public String tilesetPath;
        public int numCols;
        public float offsetX;
        public float offsetY;
        public int[][] tilePalette;
        public int[] tiles;
    }

    public static void load(final String jsonPath) {
        final Json json = Services.get(Json.class);
        final TilesetDatastore tilesets = Services.get(TilesetDatastore.class);
        final SceneDatastore scenes = Services.get(SceneDatastore.class);

        final FileHandle fileHandle = Gdx.files.internal(jsonPath);
        SceneData sceneData = json.fromJson(SceneData.class, fileHandle.readString());
        final String sceneName = fileHandle.nameWithoutExtension();

        if ((sceneData.tiles.length % sceneData.numCols) != 0) {
            throw new SerialziationException(
                format("Found {0} tiles, which can't be divided into {1} column per row", sceneData.tiles.length,
                    sceneData.numCols));
        }

        Tileset tileset = tilesets.get(sceneData.tilesetPath);
        int numRows = sceneData.tiles.length / sceneData.numCols;
        Scene scene = new Scene(tileset, sceneData.numCols, numRows, sceneData.offsetX, sceneData.offsetY);

        for (int i = 0; i < sceneData.tiles.length; i++) {
            int tileIndex = sceneData.tiles[i];
            int[] tileCoords = sceneData.tilePalette[tileIndex];
            final TextureRegion tile = tileset.getTile(tileCoords[0], tileCoords[1]);
            scene.setTile(i, tile);
        }
        scenes.add(sceneName, scene);
    }

}
