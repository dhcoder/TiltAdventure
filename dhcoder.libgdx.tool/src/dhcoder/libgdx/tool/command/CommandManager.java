package dhcoder.libgdx.tool.command;

import com.badlogic.gdx.utils.Array;
import dhcoder.support.collection.ArrayMap;
import dhcoder.support.opt.Opt;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.regex.Pattern;

import static dhcoder.libgdx.collection.CollectionUtils.toArrayList;
import static dhcoder.support.text.StringUtils.format;
import static dhcoder.support.text.StringUtils.isWhitespace;
import static java.util.regex.Pattern.CASE_INSENSITIVE;

/**
 * A collection of {@link Command}s. Provides sanity checking, like ensuring there are not conflicts, and support for
 * searching.
 */
public final class CommandManager {
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

    private final ArrayMap<String, Command> commandIdsMap = new ArrayMap<String, Command>();
    private final Array<CommandScope> commandScopes = new Array<CommandScope>();

    public void register(final CommandScope scope) {

        if (!scope.isTopLevel()) {
            throw new IllegalArgumentException(
                format("Can't register scope {0} which isn't at the top-level", scope.getFullName()));
        }

        Stack<CommandScope> scopeStack = new Stack<CommandScope>();
        scopeStack.add(scope);

        while (!scopeStack.isEmpty()) {
            CommandScope activeScope = scopeStack.pop();
            scopeStack.addAll(activeScope.getChildren());

            for (Command command : activeScope.getCommands()) {
                if (command.getId().isEmpty()) {
                    continue;
                }

                if (commandIdsMap.containsKey(command.getId())) {
                    throw new IllegalArgumentException(
                        format("Duplicate command, id={0}, name={1}", command.getId(), command.getName()));
                }

                commandIdsMap.put(command.getId(), command);
            }
        }

        commandScopes.add(scope);
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
     * <p/>
     * This generates a new list copy each time so you may wish to cache it.
     */
    public List<CommandScope> getCommandScopes() {
        return toArrayList(commandScopes);
    }
}
