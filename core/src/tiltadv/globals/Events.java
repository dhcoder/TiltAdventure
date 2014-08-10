package tiltadv.globals;

import dhcoder.support.event.Event;

/**
 * A collection of system-wide events that any component might want to listen to.
 */
public final class Events {
    public static Event onScreenTouchDown = new Event();
    public static Event onScreenTouchUp = new Event();
}
