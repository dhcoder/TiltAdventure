package dhcoder.support.event;

/**
 * A callback which is triggered when an event happens and includes the sender that initiated the event.
 */
public interface EventListener {
    void run(Object sender);
}
