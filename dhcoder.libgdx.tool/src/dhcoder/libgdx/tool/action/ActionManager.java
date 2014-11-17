package dhcoder.libgdx.tool.action;

import dhcoder.support.collection.ArrayMap;
import dhcoder.support.opt.Opt;
import dhcoder.support.text.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import static dhcoder.support.text.StringUtils.format;
import static java.util.regex.Pattern.CASE_INSENSITIVE;

/**
 * A collection of {@link Action}s. Provides sanity checking, like ensuring there are not conflicts, and support for
 * searching.
 */
public final class ActionManager {
    private static final int EXPECTED_COUNT = 100;

    /**
     * Given a query like "zya", return a pattern that matches values like "fu*zz*ySe*arch".
     *
     * @throws IllegalArgumentException if the input query has no characters.
     */
    public static Pattern toFuzzySearch(final String query) {
        if (StringUtils.isWhitespace(query)) {
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

    public static List<Action> regexSearch(final Pattern pattern, final List<Action> actionSet) {
        ArrayList<Action> matchingActions = new ArrayList<Action>();
        for (Action action : actionSet) {
            if (action.isExcludedFromSearch()) {
                continue;
            }

            if (pattern.matcher(action.getName()).matches()) {
                matchingActions.add(action);
            }
        }

        return matchingActions;
    }

    private final ArrayMap<String, Action> actionIdsMap = new ArrayMap<String, Action>(EXPECTED_COUNT);
    private final ArrayMap<ActionScope, ArrayList<Action>> scopedActionsMap =
        new ArrayMap<ActionScope, ArrayList<Action>>();

    public void register(final Action action) {
        if (actionIdsMap.containsKey(action.getId())) {
            throw new IllegalArgumentException(
                format("Duplicate command, id={0}, name={1}", action.getId(), action.getName()));
        }

        actionIdsMap.put(action.getId(), action);

        ActionScope currentScope = action.getScope();
        while (true) {
            addActionToScopeList(action, currentScope);
            if (!currentScope.getParentOpt().hasValue()) {
                break;
            }
            currentScope = currentScope.getParentOpt().getValue();
        }
    }

    public void excludeFromSearch(final Action action) {
        action.setExcludedFromSearch(true);
    }

    /**
     * Return all actions registered with this manager.
     */
    public List<Action> allActions() {
        return actionIdsMap.getValues();
    }

    /**
     * Return all commands registered with this command manager that fall under the passed in scopes.
     */
    public List<Action> scopedActions(final ActionScope... scopes) {
        for (int i = 0; i < scopes.length; i++) {
            for (int j = i + 1; j < scopes.length; j++) {
                if (scopes[i].isRelatedTo(scopes[j])) {
                    throw new IllegalArgumentException(
                        format("Redundant scopes {0} and {1} requested", scopes[i], scopes[j]));
                }
            }
        }

        ArrayList<Action> scopedActions = new ArrayList<Action>();

        Opt<ArrayList<Action>> scopedActionsOpt = Opt.withNoValue();
        for (ActionScope scope : scopes) {
            scopedActionsMap.get(scope, scopedActionsOpt);
            if (scopedActionsOpt.hasValue()) {
                scopedActions.addAll(scopedActionsOpt.getValue());
            }
        }

        return scopedActions;
    }

    private void addActionToScopeList(final Action action, final ActionScope scope) {
        if (!action.getScope().isDescendantOf(scope)) {
            throw new IllegalArgumentException(
                format("Command with scope {0} can't be associated with scope {1}", action.getScope(), scope));
        }

        ArrayList<Action> scopedActions;
        Opt<ArrayList<Action>> scopedActionsOpt = Opt.withNoValue();
        scopedActionsMap.get(scope, scopedActionsOpt);
        if (scopedActionsOpt.hasValue()) {
            scopedActions = scopedActionsOpt.getValue();
        }
        else {
            scopedActions = new ArrayList<Action>(EXPECTED_COUNT / ActionScope.EXPECTED_SIZE);
            scopedActionsMap.put(scope, scopedActions);
        }
        scopedActions.add(action);
    }
}
