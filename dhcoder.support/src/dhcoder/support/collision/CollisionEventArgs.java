package dhcoder.support.collision;

import dhcoder.support.event.EventArgs;
import dhcoder.support.memory.Poolable;

public final class CollisionEventArgs implements Poolable, EventArgs {

    private Collision collision;

    public Collision getCollision() {
        return collision;
    }

    public void setCollision(final Collision collision) {
        this.collision = collision;
    }

    @Override
    public void reset() {
        collision = null;
    }
}
