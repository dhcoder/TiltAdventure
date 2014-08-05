package dhcoder.libgdx.collision;

import dhcoder.support.memory.Poolable;

/**
 * Information about the point of impact between two {@link Collider}s.
 */
public final class Collision implements Poolable {

    // Implementation detail - CollisionSystem saves the hashmap key for this collision inside itself. It lets us avoid
    // having to allocate a key every time a new collision occurs.
    private final ColliderKey key = new ColliderKey();
    private Collider source;
    private Collider target;

    public Collider getSource() {
        return source;
    }

    public Collider getTarget() {
        return target;
    }

    @Override
    public void reset() {
        source = target = null;
        key.reset();
    }

    // Should only be called by CollisionSystem
    ColliderKey getKey() { return key; }

    // Should only be called by CollisionSystem
    void set(final Collider source, final Collider target) {
        this.source = source;
        this.target = target;
        key.set(source, target);
    }
}

