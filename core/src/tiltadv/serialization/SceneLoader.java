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

/**
 * Class that loads {@link Scene}s into our {@link SceneDatastore}.
 */
public final class SceneLoader {

    private final static class SceneData {
        public String tilesetPath;
        public int numCols;
        public int numRows;
        public float offsetX;
        public float offsetY;
        public int[] tiles;
    }

    public static void load(final String jsonPath) {
        final Json json = Services.get(Json.class);
        final TilesetDatastore tilesets = Services.get(TilesetDatastore.class);
        final SceneDatastore scenes = Services.get(SceneDatastore.class);

        final FileHandle fileHandle = Gdx.files.internal(jsonPath);
        SceneData sceneData = json.fromJson(SceneData.class, fileHandle.readString());
        final String sceneName = fileHandle.nameWithoutExtension();

        Tileset tileset = tilesets.get(sceneData.tilesetPath);
        Scene scene = new Scene(tileset, sceneData.numCols, sceneData.numRows, sceneData.offsetX, sceneData.offsetY);

        for (int i = 0; i < sceneData.tiles.length; i++) {
            int tileIndex = sceneData.tiles[i];
            final TextureRegion tile = tileset.getTile(tileIndex);
            scene.setTile(i, tile);
        }
        scenes.add(sceneName, scene);
    }

}
