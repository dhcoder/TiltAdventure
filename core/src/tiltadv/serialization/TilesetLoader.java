package tiltadv.serialization;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Json;
import tiltadv.assets.ImageDatastore;
import tiltadv.assets.Tileset;
import tiltadv.assets.TilesetDatastore;
import tiltadv.globals.Services;

/**
 * Class that serializes / deserializes {@link Tileset}s
 */
public final class TilesetLoader {

    public static void load(final String jsonPath) {
        final Json json = Services.get(Json.class);
        final ImageDatastore images = Services.get(ImageDatastore.class);
        final TilesetDatastore tilesets = Services.get(TilesetDatastore.class);

        TilesetData data = json.fromJson(TilesetData.class, Gdx.files.internal(jsonPath).readString());

        tilesets.add(jsonPath, new Tileset(images.get(data.imagePath), data.tileWidth, data.tileHeight));
    }

    private final static class TilesetData {
        public String imagePath;
        public int tileWidth;
        public int tileHeight;
    }

}
