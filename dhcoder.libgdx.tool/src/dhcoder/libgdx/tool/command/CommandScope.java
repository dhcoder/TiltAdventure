package dhcoder.libgdx.tool.command;

import dhcoder.support.collection.ArrayMap;
import dhcoder.support.opt.Opt;
import dhcoder.support.text.StringUtils;

import java.util.ArrayList;
import java.util.List;

import static dhcoder.support.text.StringUtils.format;
import static dhcoder.support.text.StringUtils.isWhitespace;

/**
 * Scope for a command, which can be used to narrow down the available commands at any given time.
 */
public final class CommandScope {
    /**
     * How many number of commands we expect to register to this scope. You can register more - this just determines
     * initial preallocation sizes.
     */
    public static final int EXPECTED_SIZE = 10;

    private final Opt<CommandScope> parentOpt = Opt.withNoValue();
    private final List<CommandScope> children = new ArrayList<CommandScope>(0);
    private final String name;
    private final String fullName;
    private final ArrayMap<Shortcut, Command> shortcutsCommandMap = new ArrayMap<Shortcut, Command>(EXPECTED_SIZE);
    private final ArrayMap<String, Shortcut> idShortcutsMap = new ArrayMap<String, Shortcut>(EXPECTED_SIZE);

    public CommandScope() {
        this("");
    }

    public CommandScope(final String name) {
        this.name = name;
        fullName = name;
    }

    public CommandScope(final String name, final CommandScope parentScope) {
        if (isWhitespace(name)) {
            throw new IllegalArgumentException("Can't specify an empty name for a non-root command scope");
        }

        parentOpt.set(parentScope);
        this.name = name;
        this.fullName = buildFullName();

        parentScope.children.add(this);
    }

    public Opt<CommandScope> getParentOpt() {
        return parentOpt;
    }

    public List<CommandScope> getChildren() {
        return children;
    }

    public boolean isAncestorOf(final CommandScope otherScope) {
        return (otherScope.isDescendantOf(this));
    }

    public boolean isRelatedTo(final CommandScope otherScope) {
        return (this.isAncestorOf(otherScope) || this.isDescendantOf(otherScope));
    }

    public boolean isDescendantOf(final CommandScope otherScope) {
        if (this == otherScope) {
            return true;
        }

        CommandScope currentScope = this;
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
        Opt<Command> commandOpt = Opt.withNoValue();
        shortcutsCommandMap.get(shortcut, commandOpt);
        if (commandOpt.hasValue() && commandOpt.getValue().run()) {
            return true;
        }

        for (CommandScope child : children) {
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

    void setShortcut(final Shortcut shortcut, final Command command) {
        assertValidCommand(command);
        if (shortcutsCommandMap.containsKey(shortcut)) {
            throw new IllegalArgumentException(
                format("The shortcut {0} being registered for '{1}' is already assigned to '{2}'", shortcut, command,
                    shortcutsCommandMap.get(shortcut)));
        }

        shortcutsCommandMap.put(shortcut, command);
        idShortcutsMap.put(command.getId(), shortcut);
    }

    /**
     * Get the shortcut registered with the target command (if any)
     */
    Opt<Shortcut> getShortcutOpt(final Command command) {
        assertValidCommand(command);
        Opt<Shortcut> shortcutOpt = Opt.withNoValue();
        idShortcutsMap.get(command.getId(), shortcutOpt);
        return shortcutOpt;
    }

    private void assertValidCommand(final Command command) {
        if (command.getScope() != this) {
            throw new IllegalArgumentException(StringUtils
                .format("Unexpected command {0} passed into scope {1}", command.getFullName(), this.getFullName()));
        }
    }

    private String buildFullName() {
        StringBuilder fullNameBuilder = new StringBuilder(name);
        CommandScope currentScope = this;
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
