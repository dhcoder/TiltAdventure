package tiltadv.globals.events;

import dhcoder.support.event.ArgEvent;
import dhcoder.support.event.Event;

/**
 * A collection of system-wide events that any component might want to listen to.
 */
public final class Events {
    public static final ArgEvent<TouchEventArgs> onTouchDown = new ArgEvent<TouchEventArgs>();
    public static final Event onTouchUp = new Event();
}


