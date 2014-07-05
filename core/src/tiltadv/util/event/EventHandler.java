package tiltadv.util.event;

import tiltadv.util.lambda.Action1;

/**
 * A callback which is triggered when an event happens and includes the sender that initiated the event.
 */
public interface EventHandler extends Action1 {

    @Override
    void run(Object sender);
}
