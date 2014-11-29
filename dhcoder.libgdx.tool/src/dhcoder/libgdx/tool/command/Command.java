package dhcoder.libgdx.tool.command;

import dhcoder.libgdx.tool.scene2d.CommandListener;
import dhcoder.support.opt.Opt;
import dhcoder.support.text.StringUtils;

/**
 * An action is essentially a callback with some useful meta information like name and description.
 */
public final class Command {

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
    private final CommandScope scope;
    private final String name;
    private final String fullName;
    private final String description;
    private final RunCallback runCallback;
    private boolean excludeFromSearch;
    private ActiveCallback activeCallback = ALWAYS_ACTIVE;

    /**
     * Create a standard command, including all manner of metadata useful for presenting information to the user.
     * You should use this constructor if you plan to register this command with the {@link CommandManager}, which is
     * useful if you want to be include this command in a list of all commands easily searchable by the user via the
     * UI.
     */
    public Command(final String id, final CommandScope scope, final String name, final String description,
        final RunCallback runCallback) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.runCallback = runCallback;
        this.scope = scope;

        this.fullName = scope.getScopedName(this);

        scope.addCommand(this);
    }

    /**
     * Create an anonymous command, essentially a wrapper around a callback with a scope. This is useful for commands
     * that should be kept local to a widget (like up/down/backspace handling), where it can still be taken advantage of
     * by {@link CommandListener}, but doesn't need the extra heavyweight search functionality you get by registering
     * it with a {@link CommandManager}
     *
     * If a lightweight command is registered with a {@link CommandManager}, an exception will be thrown.
     */
    public Command(final CommandScope scope, final RunCallback runCallback) {
        this("", scope, "", "", runCallback);
    }

    public Command setShortcut(final Shortcut shortcut) {
        scope.setShortcut(shortcut, this);
        return this;
    }

    public Opt<Shortcut> getShortcutOpt() {
        return scope.getShortcutOpt(this);
    }

    public CommandScope getScope() {
        return scope;
    }

    public String getFullName() {
        return fullName;
    }

    public Command setActiveCallback(final ActiveCallback activeCallback) {
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
