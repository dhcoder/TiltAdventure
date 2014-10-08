package tiltadv.globals.events;

import dhcoder.support.event.EventArgs;

public final class TouchEventArgs implements EventArgs {
    public final int X;
    public final int Y;

    public TouchEventArgs(final int x, final int y) {
        X = x;
        Y = y;
    }
}
