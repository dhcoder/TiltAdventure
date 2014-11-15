package dhcoder.libgdx.tool.command;

import dhcoder.support.collection.ArrayMap;
import dhcoder.support.opt.Opt;

import static dhcoder.support.text.StringUtils.format;

/**
 * A collection of {@link Command}s. Provides sanity checking, like ensuring there are not conflicts, and organization,
 * like keyboard shortcut access to commands.
 * <p/>
 * In a UI, it is recommended that you maintain a set of global commands and a bunch of smaller sets of local commands
 * associated with whatever context has focus.
 */
public final class Commands {
    private static final int EXPECTED_COMMAND_COUNT = 20;
    private final ArrayMap<String, Command> commandIds = new ArrayMap<String, Command>(EXPECTED_COMMAND_COUNT);
    private final ArrayMap<Shortcut, Command> shortcuts = new ArrayMap<Shortcut, Command>(EXPECTED_COMMAND_COUNT);

    public void registerCommand(final Command command) {
        if (commandIds.containsKey(command.getId())) {
            throw new IllegalArgumentException(
                format("Duplicate command, id={0}, name={1}", command.getId(), command.getName()));
        }

        commandIds.put(command.getId(), command);
    }

    public void registerShortcut(final Shortcut shortcut, final Command targetCommand) {
        if (shortcuts.containsKey(shortcut)) {
            throw new IllegalArgumentException(
                format("The shortcut {0} being registered for '{1}' is already assigned to '{2}'", shortcut,
                    targetCommand, shortcuts.get(shortcut)));
        }

        shortcuts.put(shortcut, targetCommand);
    }

    public boolean handleInput(final Shortcut shortcut) {
        Opt<Command> commandOpt = Opt.withNoValue();
        shortcuts.get(shortcut, commandOpt);
        if (commandOpt.hasValue()) {
            if (commandOpt.getValue().run()) {
                return true;
            }
        }

        return false;
    }

}
