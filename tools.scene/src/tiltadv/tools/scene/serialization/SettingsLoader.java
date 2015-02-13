package tiltadv.tools.scene.serialization;

import com.google.gson.Gson;
import tiltadv.tools.scene.AppSettings;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Class that loads global settings for this application.
 */
public final class SettingsLoader {

    public static AppSettings load(final Gson gson, final Path settingsPath) throws IOException {
        return gson.fromJson(new String(Files.readAllBytes(settingsPath)), AppSettings.class);
    }
}
