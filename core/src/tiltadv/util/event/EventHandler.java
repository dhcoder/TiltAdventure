package tiltadv.util.event;

import tiltadv.util.lambda.Action1;

/**
 * A class which can register listeners which are triggered on an event happening.
 */
public interface EventHandler extends Action1 {

    @Override
    void run(Object sender);
}
