package tiltadv.tools.scene.serialization;

import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Class that loads global settings for this application.
 */
public final class SettingsLoader {

    public final static class AppSettings {
        private int[] size;
        private String assetDir;

        public int getWidth() { return size[0]; }

        public int getHeight() { return size[1]; }

        public File getAssetDir() { return new File(assetDir); }

        public File getTilesetDir() { return new File(getAssetDir(), "data/tilesets"); }
    }

    public static AppSettings load(final Gson gson, final Path settingsPath) throws IOException {
        return gson.fromJson(new String(Files.readAllBytes(settingsPath)), AppSettings.class);
    }
}
