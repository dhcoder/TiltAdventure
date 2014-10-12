package tiltadv.globals;

import dhcoder.libgdx.entity.Entity;
import dhcoder.support.event.Event;

/**
 * A colleciton of global events.
 */
public final class Events {
    public static Event<Entity> onTargetSelected = new Event<Entity>();
    public static Event<Entity> onTargetCleared = new Event<Entity>();
}
