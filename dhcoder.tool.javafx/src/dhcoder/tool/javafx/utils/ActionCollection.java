package dhcoder.tool.javafx.utils;

import dhcoder.support.opt.Opt;
import org.controlsfx.control.action.Action;
import org.controlsfx.control.action.ActionGroup;

import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Stack;
import java.util.regex.Pattern;

import static dhcoder.support.text.StringUtils.format;
import static dhcoder.support.text.StringUtils.isWhitespace;
import static java.util.regex.Pattern.CASE_INSENSITIVE;

/**
 * A flat, searchable collection of {@link Action}s.
 */
public final class ActionCollection extends AbstractCollection<Action> {

    public static final String PROPERTY_UNSEARCHABLE = "unsearchable";
    public static final String PROPERTY_ID = "id";

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

    public static void setUnsearchable(final Action action) {
        action.getProperties().put(PROPERTY_UNSEARCHABLE, true);
    }

    public static void setId(final Action action, final String id) {
        action.getProperties().put(PROPERTY_ID, id);
    }

    private final ArrayList<Action> actions = new ArrayList<>();
    private final Map<Action, String> fullNames = new HashMap<>();
    private final Map<String, Action> ids = new HashMap<>();
    private final Stack<String> stackPrefixes = new Stack<>();
    public static final Pattern SERACH_ALL = Pattern.compile(".*");

    public boolean addGroup(final ActionGroup actionGroup) {
        if (stackPrefixes.size() == 0) {
            stackPrefixes.push(actionGroup.getText());
        }
        else {
            stackPrefixes.push(format("{0}.{1}", stackPrefixes.peek(), actionGroup.getText()));
        }

        boolean groupAdded = addAll(actionGroup.getActions());

        stackPrefixes.pop();

        return groupAdded;
    }

    public Opt<Action> findById(final String id) {
        return Opt.ofNullable(ids.get(id));
    }

    public String getScopedName(final Action action) {
        return fullNames.get(action);
    }

    public Collection<Action> searchAll() {
        return search(SERACH_ALL);
    }

    public Collection<Action> search(final Pattern regex) {
        ArrayList<Action> matchingCommands = new ArrayList<>();
        for (Action action : actions) {
            if (action.getProperties().containsKey(PROPERTY_UNSEARCHABLE)) {
                continue;
            }

            if (regex.matcher(fullNames.get(action)).matches()) {
                matchingCommands.add(action);
            }
        }

        return matchingCommands;
    }

    @Override
    public Iterator<Action> iterator() {
        return actions.iterator();
    }

    @Override
    public int size() {
        return actions.size();
    }

    @Override
    public boolean add(final Action action) {
        if (action instanceof ActionGroup) {
            return addGroup((ActionGroup)action);
        }

        if (stackPrefixes.size() > 0) {
            String prefix = stackPrefixes.peek();
            fullNames.put(action, format("{0}: {1}", prefix, action.getText()));
        }
        else {
            fullNames.put(action, action.getText());
        }

        String id = (String)action.getProperties().get(PROPERTY_ID);
        if (id != null) {
            ids.put(id, action);
        }

        return actions.add(action);
    }
}
