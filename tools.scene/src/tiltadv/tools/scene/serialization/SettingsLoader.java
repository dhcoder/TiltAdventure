package tiltadv.tools.scene.serialization;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;

/**
 * Class that loads global settings for this application.
 */
public final class SettingsLoader {

    public final static class AppSettings {
        public boolean firstRun;
    }

    public static AppSettings load(final Json json, final String jsonPath) {
        final FileHandle fileHandle = Gdx.files.internal(jsonPath);
        return json.fromJson(AppSettings.class, fileHandle.readString());
    }
}
