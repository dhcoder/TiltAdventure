package dhcoder.libgdx.tool.command;

/**
 * A command, essentially a callback with some useful meta information like name and description.
 */
public final class Command {
    private final String id;
    private final String name;
    private final String description;
    private final Runnable action;
    private boolean enabled = true;

    public Command(final String id, final String name, final String description, final Runnable action) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.action = action;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(final boolean enabled) {
        this.enabled = enabled;
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

        action.run();
        return true;
    }

    @Override
    public String toString() {
        return name;
    }
}
