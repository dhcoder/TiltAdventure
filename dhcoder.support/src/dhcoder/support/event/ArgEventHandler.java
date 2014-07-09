package dhcoder.support.event;

import dhcoder.support.lambda.Action2;

/**
 * A callback which is triggered when an event happens and includes the sender that initiated the event plus any
 * relevant arguments.
 */
public interface ArgEventHandler<T extends EventArgs> extends Action2<Object, T> {

    @Override
    void run(Object sender, T args);
}
