package dhcoder.tool.event;

/**
 * Base class for events used in our tools which should have a sender.
 */
public abstract class ToolSenderEventArgs<S> extends ToolEventArgs {
    private final S sender;

    public ToolSenderEventArgs(final S sender) {
        this.sender = sender;
    }

    public S getSender() {
        return sender;
    }
}
