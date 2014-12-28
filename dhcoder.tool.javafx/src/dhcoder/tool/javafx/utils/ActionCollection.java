package dhcoder.tool.javafx.utils;

import dhcoder.support.text.StringUtils;
import org.controlsfx.control.action.Action;
import org.controlsfx.control.action.ActionGroup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.regex.Pattern;

import static dhcoder.support.text.StringUtils.*;
import static dhcoder.support.text.StringUtils.isWhitespace;
import static java.util.regex.Pattern.CASE_INSENSITIVE;
/**
 * A flat, searchable collection of {@link Action}s.
 */
public final class ActionCollection {

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

    private final Collection<Action> allActions = new ArrayList<>();
    private final Map<Action, String> fullNames = new HashMap<>();
    private final Set<Action> unsearchableActions = new HashSet<Action>();
    private final Stack<String> stackPrefixes = new Stack<>();

    public void add(final Action... actions) {
        add(Arrays.asList(actions));
    }

    public void addGroup(final ActionGroup actionGroup) {

        if (stackPrefixes.size() == 0) {
            stackPrefixes.push(actionGroup.getText());
        }
        else {
            stackPrefixes.push(format("{0}.{1}", stackPrefixes.peek(), actionGroup.getText()));
        }

        add(actionGroup.getActions());

        stackPrefixes.pop();
    }

    public void add(final Iterable<Action> actions) {
        for (Action action : actions) {
            if (action instanceof ActionGroup) {
                addGroup((ActionGroup)action);
                continue;
            }
            allActions.add(action);

            if (stackPrefixes.size() > 0) {
                String prefix = stackPrefixes.peek();
                fullNames.put(action, format("{0}: {1}", prefix, action.getText()));
            }
            else {
                fullNames.put(action, action.getText());
            }
        }
    }

    public void setUnsearchable(final Action action) {
        unsearchableActions.add(action);
    }

    public String getScopedName(final Action action) {
        return fullNames.get(action);
    }

    public Iterable<Action> filter(final Pattern regex) {
            ArrayList<Action> matchingCommands = new ArrayList<>();
            for (Action action : allActions) {
                if (unsearchableActions.contains(action)) {
                    continue;
                }

                if (regex.matcher(fullNames.get(action)).matches()) {
                    matchingCommands.add(action);
                }
            }

            return matchingCommands;
        }


    }
}
