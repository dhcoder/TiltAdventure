package dhcoder.libgdx.tool.command;

import dhcoder.support.text.StringUtils;

/**
 * A command, essentially a callback with some useful meta information like name and description.
 */
public final class Command {

    public interface RunCallback {
        void run();
    }

    public interface TestActiveCallback {
        boolean isActive();
    }

    private static final TestActiveCallback ALWAYS_ACTIVE = new TestActiveCallback() {
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
    private TestActiveCallback testActiveCallback = ALWAYS_ACTIVE;

    public Command(final String id, final CommandScope scope, final String name, final String description,
        final RunCallback runCallback) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.runCallback = runCallback;
        this.fullName = StringUtils.format("{0}: {1}", scope.getFullName(), name);
        this.scope = scope;
    }

    public CommandScope getScope() {
        return scope;
    }

    public String getFullName() {
        return fullName;
    }

    public Command setTestActiveCallback(final TestActiveCallback testActiveCallback) {
        this.testActiveCallback = testActiveCallback;
        return this;
    }

    public boolean isEnabled() {
        return testActiveCallback.isActive();
    }

    public String getDescription() {
        return description;
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
        return name;
    }
}
