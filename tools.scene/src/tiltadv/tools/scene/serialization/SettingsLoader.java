package tiltadv.tools.scene.serialization;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;

import java.io.File;

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

    public static AppSettings load(final Json json, final String jsonPath) {
        final FileHandle fileHandle = Gdx.files.internal(jsonPath);
        return json.fromJson(AppSettings.class, fileHandle.readString());
    }
}
