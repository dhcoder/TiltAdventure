package dhcoder.libgdx.tool.command;

import dhcoder.support.opt.Opt;

import java.util.ArrayList;
import java.util.List;

import static dhcoder.support.text.StringUtils.isWhitespace;

/**
 * Scope for a command, which can be used to narrow down the available commands at any given time.
 */
public final class CommandScope {
    private final Opt<CommandScope> parentOpt = Opt.withNoValue();
    private final List<CommandScope> children = new ArrayList<CommandScope>(0);
    private final String name;
    private final String fullName;

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
        CommandScope currentScope = otherScope;
        while (currentScope.parentOpt.hasValue()) {
            currentScope = currentScope.parentOpt.getValue();
            if (currentScope == this) {
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

    @Override
    public String toString() {
        return (!fullName.isEmpty() ? fullName : "<ROOT>");
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
