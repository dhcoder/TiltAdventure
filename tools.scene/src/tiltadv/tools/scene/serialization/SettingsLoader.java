package tiltadv.tools.scene.serialization;

import com.badlogic.gdx.utils.Json;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Class that loads global settings for this application.
 */
public final class SettingsLoader {

    public final static class AppSettings {
        public int[] size;
        public String assetPath;

        public int getWidth() { return size[0]; }
        public int getHeight() { return size[1]; }
        public File getAssetPath() { return new File(assetPath); }
        public File getTilesetPath() {
            return new File(assetPath + "/data/tilesets");
        }
    }

    public static AppSettings load(final Json json, final Path settingsPath) throws IOException {
        return json.fromJson(AppSettings.class, new String(Files.readAllBytes(settingsPath)));
    }
}
