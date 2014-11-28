package dhcoder.libgdx.tool.command;

import dhcoder.support.collection.ArrayMap;
import dhcoder.support.opt.Opt;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import static dhcoder.support.text.StringUtils.format;
import static dhcoder.support.text.StringUtils.isWhitespace;
import static java.util.regex.Pattern.CASE_INSENSITIVE;

/**
 * A collection of {@link Command}s. Provides sanity checking, like ensuring there are not conflicts, and support for
 * searching.
 */
public final class CommandManager {
    private static final int EXPECTED_COUNT = 100;

    /**
     * Given a query like "zyar", return a pattern that matches values like "fu(z)z(y)Se(ar)ch".
     *
     * @throws IllegalArgumentException if the input query has no characters.
     */
    public static Pattern toFuzzySearch(final String query) {
        if (isWhitespace(query)) {
            throw new IllegalArgumentException("Invalid query is all whitespace");
        }

        // Turn a query like "abc" into regex pattern ".*a.*b.*c.*"
        StringBuilder patternBuilder = new StringBuilder(query.length() * 3 + 2);
        patternBuilder.append(".*");
        for (int i = 0; i < query.length(); i++) {
            char letter = query.charAt(i);
            patternBuilder.append(letter);
            patternBuilder.append(".*");
        }

        Pattern queryPattern = Pattern.compile(patternBuilder.toString(), CASE_INSENSITIVE);
        return queryPattern;
    }

    public static List<Command> regexSearch(final Pattern pattern, final List<Command> commandSet) {
        ArrayList<Command> matchingCommands = new ArrayList<Command>();
        for (Command command : commandSet) {
            if (command.isExcludedFromSearch()) {
                continue;
            }

            if (pattern.matcher(command.getFullName()).matches()) {
                matchingCommands.add(command);
            }
        }

        return matchingCommands;
    }

    private final ArrayMap<String, Command> commandIdsMap = new ArrayMap<String, Command>(EXPECTED_COUNT);
    private final ArrayMap<CommandScope, ArrayList<Command>> scopedCommandsMap =
        new ArrayMap<CommandScope, ArrayList<Command>>();

    public Command register(final Command command) {
        if (isWhitespace(command.getId())) {
            throw new IllegalArgumentException("Attempting to register a lightweight (ID-less) command");
        }

        if (commandIdsMap.containsKey(command.getId())) {
            throw new IllegalArgumentException(
                format("Duplicate command, id={0}, name={1}", command.getId(), command.getName()));
        }

        commandIdsMap.put(command.getId(), command);

        CommandScope currentScope = command.getScope();
        while (true) {
            addToScope(currentScope, command);
            if (currentScope.isTopLevel()) {
                break;
            }
            currentScope = currentScope.getParent();
        }

        return command;
    }

    public void excludeFromSearch(final Command command) {
        command.setExcludedFromSearch(true);
    }

    public Opt<Command> findCommand(final String id) {
        Opt<Command> commandOpt = Opt.withNoValue();
        commandIdsMap.get(id, commandOpt);
        return commandOpt;
    }

    /**
     * Return all actions registered with this manager.
     */
    public List<Command> allCommands() {
        return commandIdsMap.getValues();
    }

    public List<Command> searchableCommands() {
        List<Command> allCommands = allCommands();
        List<Command> searchableCommands = new ArrayList<Command>(allCommands.size());
        for (Command command : allCommands) {
            if (!command.isExcludedFromSearch()) {
                searchableCommands.add(command);
            }
        }
        return searchableCommands;
    }

    /**
    public List<Command> scopedCommands(final CommandScope... scopes) {
        for (int i = 0; i < scopes.length; i++) {
            for (int j = i + 1; j < scopes.length; j++) {
                if (scopes[i].isRelatedTo(scopes[j])) {
                    throw new IllegalArgumentException(
                        format("Redundant scopes {0} and {1} requested", scopes[i], scopes[j]));
                }
            }
        }

        ArrayList<Command> scopedCommands = new ArrayList<Command>();

        Opt<ArrayList<Command>> scopedCommandsOpt = Opt.withNoValue();
        for (CommandScope scope : scopes) {
            scopedCommandsMap.get(scope, scopedCommandsOpt);
            if (scopedCommandsOpt.hasValue()) {
                scopedCommands.addAll(scopedCommandsOpt.getValue());
            }
        }

        return scopedCommands;
    }*/

    public boolean handle(final Shortcut shortcut) {
        for (CommandScope scope : getCommandScopes()) {
            if (scope.handle(shortcut)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Return all top-level command scopes registered with this command manager.
     *
     * This generates a new list copy each time so you may wish to cache it.
     */
    public List<CommandScope> getCommandScopes() {
        List<CommandScope> allScopes = scopedCommandsMap.getKeys();
        List<CommandScope> topLevelScopes = new ArrayList<CommandScope>(allScopes.size());
        for (CommandScope scope : allScopes) {
            if (scope.isTopLevel()) {
                topLevelScopes.add(scope);
            }
        }
        return topLevelScopes;
    }

    private void addToScope(final CommandScope scope, final Command command) {
        if (!command.getScope().isDescendantOf(scope)) {
            throw new IllegalArgumentException(
                format("Command with scope {0} can't be associated with scope {1}", command.getScope(), scope));
        }

        ArrayList<Command> scopedCommands;
        Opt<ArrayList<Command>> scopedCommandsOpt = Opt.withNoValue();
        scopedCommandsMap.get(scope, scopedCommandsOpt);
        if (scopedCommandsOpt.hasValue()) {
            scopedCommands = scopedCommandsOpt.getValue();
        }
        else {
            scopedCommands = new ArrayList<Command>(EXPECTED_COUNT / CommandScope.EXPECTED_SIZE);
            scopedCommandsMap.put(scope, scopedCommands);
        }
        scopedCommands.add(command);
    }
}
