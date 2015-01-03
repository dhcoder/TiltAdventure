package dhcoder.tool.game.serialization;

import com.google.gson.Gson;
import dhcoder.tool.game.model.Tileset;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Class that loads global settings for this application.
 */
public final class TilesetLoader {

    private final static class TilesetData {
        public String imagePath;
        public int tileWidth;
        public int tileHeight;
    }

    public static Tileset load(final Gson gson, final Path tilesetPath) throws IOException {
        TilesetData data = gson.fromJson(new String(Files.readAllBytes(tilesetPath)), TilesetData.class);
        return new Tileset(Paths.get(data.imagePath), data.tileWidth, data.tileHeight);
    }
}
