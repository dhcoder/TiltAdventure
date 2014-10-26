package tiltadv.serialization;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Json;
import tiltadv.assets.TileDatastore;
import tiltadv.assets.TileGroup;
import tiltadv.assets.Tileset;
import tiltadv.assets.TilesetDatastore;
import tiltadv.globals.Services;

import java.util.ArrayList;

/**
 * Class that loads {@link TextureRegion}s into our {@link TileDatastore}.
 */
public final class TilesLoader {

    private final static class TileGroupData {
        public String tilesetPath;
        public ArrayList<TileData> tiles;
    }

    private final static class TileData {
        public String name;
        public int[] coord;
    }

    public static void load(final String jsonPath) {
        final Json json = Services.get(Json.class);
        final TilesetDatastore tilesets = Services.get(TilesetDatastore.class);
        final TileDatastore tiles = Services.get(TileDatastore.class);

        final FileHandle fileHandle = Gdx.files.internal(jsonPath);
        TileGroupData groupData = json.fromJson(TileGroupData.class, fileHandle.readString());

        Tileset tileset = tilesets.get(groupData.tilesetPath);
        Texture tileTexture = tileset.getTexture();
        int tileW = tileset.getTileWidth();
        int tileH = tileset.getTileHeight();
        String groupName = fileHandle.nameWithoutExtension();

        TileGroup tileGroup = new TileGroup();
        final int numTiles = groupData.tiles.size();
        for (int i = 0; i < numTiles; ++i) {
            TileData tileData = groupData.tiles.get(i);
            tileGroup.add(tileData.name,
                new TextureRegion(tileTexture, tileData.coord[0] * tileW, tileData.coord[1] * tileH, tileW, tileH));
        }

        tiles.add(groupName, tileGroup);
    }

}
