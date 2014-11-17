package dhcoder.libgdx.tool.action;

import dhcoder.support.opt.Opt;
import dhcoder.support.text.StringUtils;

/**
 * An action is essentially a callback with some useful meta information like name and description.
 */
public final class Action {

    public interface RunCallback {
        void run();
    }

    public interface ActiveCallback {
        boolean isActive();
    }

    private static final ActiveCallback ALWAYS_ACTIVE = new ActiveCallback() {
        @Override
        public boolean isActive() {
            return true;
        }
    };

    private final String id;
    private final ActionScope scope;
    private final String name;
    private final String fullName;
    private final String description;
    private final RunCallback runCallback;
    private boolean excludeFromSearch;
    private ActiveCallback activeCallback = ALWAYS_ACTIVE;

    public Action(final String id, final ActionScope scope, final String name, final String description,
        final RunCallback runCallback) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.runCallback = runCallback;
        this.scope = scope;

        this.fullName = scope.getScopedActionName(this);
    }

    public void setShortcut(final Shortcut shortcut) {
        scope.setShortcut(shortcut, this);
    }

    public Opt<Shortcut> getShortcutOpt() {
        return scope.getShortcutOpt(this);
    }

    public ActionScope getScope() {
        return scope;
    }

    public String getFullName() {
        return fullName;
    }

    public Action setActiveCallback(final ActiveCallback activeCallback) {
        this.activeCallback = activeCallback;
        return this;
    }

    public boolean isEnabled() {
        return activeCallback.isActive();
    }

    public String getDescription() {
        return description;
    }

    void setExcludedFromSearch(final boolean excludedFromSearch) {
        this.excludeFromSearch = excludedFromSearch;
    }

    boolean isExcludedFromSearch() {
        return excludeFromSearch;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public boolean run() {
        if (!isEnabled()) {
            return false;
        }

        runCallback.run();
        return true;
    }

    @Override
    public String toString() {
        return StringUtils.format("{0} [{1}]", name, id);
    }
}
