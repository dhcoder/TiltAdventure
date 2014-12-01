package dhcoder.tool.libgdx.serialization;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.utils.Json;
import dhcoder.tool.command.Command;
import dhcoder.tool.command.CommandManager;
import dhcoder.tool.command.Shortcut;
import dhcoder.support.opt.Opt;

import java.text.ParseException;

import static dhcoder.support.text.StringUtils.format;

/**
 * Class that helps load shortcuts from a file
 */
public final class ShortcutsLoader {

    private final static class ShortcutGroupData {
        public ShortcutData[] shortcuts;
    }

    private final static class ShortcutData {
        public String shortcut;
        public String commandId;
    }

    public static final String TAG_SHORTCUT = "Shortcut";

    public static void load(final Json json, final CommandManager commandManager, final String jsonPath) {
        final FileHandle fileHandle = Gdx.files.internal(jsonPath);
        ShortcutGroupData shortcutGroupData = json.fromJson(ShortcutGroupData.class, fileHandle.readString());

        for (int i = 0; i < shortcutGroupData.shortcuts.length; ++i) {
            ShortcutData shortcutData = shortcutGroupData.shortcuts[i];
            final Opt<Command> commandOpt = commandManager.findCommand(shortcutData.commandId);

            if (!commandOpt.hasValue()) {
                Gdx.app.error(TAG_SHORTCUT, format("Unknown command id \"{0}\", skipping...", shortcutData.commandId));
            }

            try {
                Shortcut shortcut = Shortcut.fromString(shortcutData.shortcut);
                commandOpt.getValue().setShortcut(shortcut);
            } catch (ParseException e) {
                Gdx.app.error(TAG_SHORTCUT, format("Invalid shortcut \"{0}\", skipping...", shortcutData.shortcut));
            }
        }
    }

}
