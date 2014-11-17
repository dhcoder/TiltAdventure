package dhcoder.libgdx.tool.action;

import dhcoder.support.collection.ArrayMap;
import dhcoder.support.opt.Opt;
import dhcoder.support.text.StringUtils;

import java.util.ArrayList;
import java.util.List;

import static dhcoder.support.text.StringUtils.format;
import static dhcoder.support.text.StringUtils.isWhitespace;

/**
 * Scope for an action, which can be used to narrow down the available actions at any given time.
 */
public final class ActionScope {
    /**
     * How many number of actions we expect to register to this scope. You can register more - this just determines
     * initial sizes for preallocation.
     */
    public static final int EXPECTED_SIZE = 10;

    private final Opt<ActionScope> parentOpt = Opt.withNoValue();
    private final List<ActionScope> children = new ArrayList<ActionScope>(0);
    private final String name;
    private final String fullName;
    private final ArrayMap<Shortcut, Action> shortcutActionsMap = new ArrayMap<Shortcut, Action>(EXPECTED_SIZE);
    private final ArrayMap<String, Shortcut> idShortcutsMap = new ArrayMap<String, Shortcut>(EXPECTED_SIZE);

    public ActionScope() {
        this("");
    }

    public ActionScope(final String name) {
        this.name = name;
        fullName = name;
    }

    public ActionScope(final ActionScope parentScope) {
        this("", parentScope);
    }

    public ActionScope(final String name, final ActionScope parentScope) {
        parentOpt.set(parentScope);
        this.name = name;
        this.fullName = buildFullName();

        parentScope.children.add(this);
    }

    public Opt<ActionScope> getParentOpt() {
        return parentOpt;
    }

    public List<ActionScope> getChildren() {
        return children;
    }

    public boolean isAncestorOf(final ActionScope otherScope) {
        return (otherScope.isDescendantOf(this));
    }

    public boolean isRelatedTo(final ActionScope otherScope) {
        return (this.isAncestorOf(otherScope) || this.isDescendantOf(otherScope));
    }

    public boolean isDescendantOf(final ActionScope otherScope) {
        if (this == otherScope) {
            return true;
        }

        ActionScope currentScope = this;
        while (currentScope.parentOpt.hasValue()) {
            currentScope = currentScope.parentOpt.getValue();
            if (currentScope == otherScope) {
                return true;
            }
        }
        return false;
    }

    public String getName() {
        return name;
    }

    public String getFullName() {
        return fullName;
    }

    public boolean handleShortcut(final Shortcut shortcut) {
        Opt<Action> actionOpt = Opt.withNoValue();
        shortcutActionsMap.get(shortcut, actionOpt);
        if (actionOpt.hasValue() && actionOpt.getValue().run()) {
            return true;
        }

        for (ActionScope child : children) {
            if (child.handleShortcut(shortcut)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public String toString() {
        return (!fullName.isEmpty() ? fullName : "<ROOT>");
    }

    String getScopedActionName(final Action action) {
        return !isWhitespace(fullName) ? format("{0}: {1}", fullName, action.getName()) : action.getName();
    }

    void setShortcut(final Shortcut shortcut, final Action action) {
        assertValid(action);
        if (shortcutActionsMap.containsKey(shortcut)) {
            throw new IllegalArgumentException(
                format("The shortcut {0} being registered for '{1}' is already assigned to '{2}'", shortcut, action,
                    shortcutActionsMap.get(shortcut)));
        }

        shortcutActionsMap.put(shortcut, action);
        idShortcutsMap.put(action.getId(), shortcut);
    }

    /**
     * Get the shortcut registered with the target command (if any)
     */
    Opt<Shortcut> getShortcutOpt(final Action action) {
        assertValid(action);
        Opt<Shortcut> shortcutOpt = Opt.withNoValue();
        idShortcutsMap.get(action.getId(), shortcutOpt);
        return shortcutOpt;
    }

    private void assertValid(final Action action) {
        if (action.getScope() != this) {
            throw new IllegalArgumentException(StringUtils
                .format("Unexpected command {0} passed into scope {1}", action.getFullName(), fullName));
        }
    }

    private String buildFullName() {
        StringBuilder fullNameBuilder = new StringBuilder(name);
        ActionScope currentScope = this;
        while (currentScope.parentOpt.hasValue()) {
            currentScope = currentScope.parentOpt.getValue();
            if (!currentScope.name.isEmpty()) {
                fullNameBuilder.insert(0, '.');
                fullNameBuilder.insert(0, currentScope.name);
            }
        }
        return fullNameBuilder.toString();
    }
}
