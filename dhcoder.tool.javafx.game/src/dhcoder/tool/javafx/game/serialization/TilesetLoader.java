package dhcoder.tool.game.serialization;

import com.google.gson.Gson;
import dhcoder.tool.game.model.Tileset;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

/**
 * Class that loads global settings for this application.
 */
public final class TilesetLoader {

    private final static class TilesetData {
        public String imagePath;
        public int tileWidth;
        public int tileHeight;
    }

    public static Tileset load(final Gson gson, final File assetDir, final File tilesetFile) throws IOException {
        TilesetData data = gson.fromJson(new String(Files.readAllBytes(tilesetFile.toPath())), TilesetData.class);
        File imageFile = new File(assetDir, data.imagePath);
        return new Tileset(imageFile, data.tileWidth, data.tileHeight);
    }
}
