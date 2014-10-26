package tiltadv.serialization;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Json;
import tiltadv.assets.ImageCollection;
import tiltadv.assets.Tileset;
import tiltadv.assets.TilesetCollection;
import tiltadv.globals.Services;

/**
 * Class that serializes / deserializes {@link Tileset}s
 */
public final class AnimationsSerializer {

    public static void load(final String jsonPath) {
        final Json json = Services.get(Json.class);
        final ImageCollection images = Services.get(ImageCollection.class);
        final TilesetCollection tilesets = Services.get(TilesetCollection.class);

        Data data = json.fromJson(AnimationsSerializer.Data.class, Gdx.files.internal(jsonPath).readString());

        tilesets.add(jsonPath, new Tileset(images.get(data.imagePath), data.tileWidth, data.tileHeight));
    }

    private final static class Data {
        public String imagePath;
        public int tileWidth;
        public int tileHeight;
    }

}
