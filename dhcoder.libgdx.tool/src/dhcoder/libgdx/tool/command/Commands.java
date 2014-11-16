package dhcoder.libgdx.tool.command;

import dhcoder.support.collection.ArrayMap;
import dhcoder.support.opt.Opt;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import static dhcoder.support.text.StringUtils.format;
import static java.util.regex.Pattern.CASE_INSENSITIVE;

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
        if (commandOpt.hasValue() && commandOpt.getValue().run()) {
            return true;
        }

        return false;
    }

    /**
     * Given a query like "zya", find commands with matching names, like "fu*zz*ySe*arch", and place them into the
     * passed in list.
     */
    public List<Command> fuzzySearch(final String query) {
        if (query.length() == 0) {
            return new ArrayList<Command>();
        }

        ArrayList<Command> matchingCommands = new ArrayList<Command>();
        // Turn a query like "abc" into regex pattern ".*a.*b.*c.*"
        StringBuilder patternBuilder = new StringBuilder(query.length() * 3 + 2);
        patternBuilder.append(".*");
        for (int i = 0; i < query.length(); i++) {
            char letter = query.charAt(i);
            patternBuilder.append(letter);
            patternBuilder.append(".*");
        }
        Pattern queryPattern = Pattern.compile(patternBuilder.toString(), CASE_INSENSITIVE);
        for (Command command : commandIds.getValues()) {
            if (queryPattern.matcher(command.getName()).matches()) {
                matchingCommands.add(command);
            }
        }

        return matchingCommands;
    }
}
