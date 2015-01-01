package dhcoder.tool.javafx.libgdx.serialization;

import com.badlogic.gdx.utils.Json;
import dhcoder.support.opt.Opt;
import dhcoder.tool.javafx.utils.ActionCollection;
import javafx.scene.input.KeyCombination;
import org.controlsfx.control.action.Action;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

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

    public static void load(final Json json, final ActionCollection actionCollection, final Path shortcutsPath)
        throws IOException {
        ShortcutGroupData shortcutGroupData =
            json.fromJson(ShortcutGroupData.class, new String(Files.readAllBytes(shortcutsPath)));

        for (int i = 0; i < shortcutGroupData.shortcuts.length; ++i) {
            ShortcutData shortcutData = shortcutGroupData.shortcuts[i];
            final Opt<Action> actionOpt = actionCollection.findById(shortcutData.commandId);

            if (!actionOpt.hasValue()) {
                System.err.println(format("Unknown command id \"{0}\", skipping...", shortcutData.commandId));
            }

            try {
                KeyCombination shortcut = KeyCombination.valueOf(shortcutData.shortcut);
                actionOpt.getValue().setAccelerator(shortcut);
            } catch (IllegalArgumentException e) {
                System.err.println(format("Unrecognized shortcut \"{0}\", skipping...", shortcutData.shortcut));
            }
        }
    }

}
