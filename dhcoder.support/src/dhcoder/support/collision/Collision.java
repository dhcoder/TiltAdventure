package dhcoder.support.collision;

import dhcoder.support.collection.Key2;
import dhcoder.support.memory.Poolable;

/**
 * Information about the point of impact between two {@link Collider}s.
 */
public final class Collision implements Poolable {

    private Collider source;
    private Collider target;
    // Implementation detail - Pool puts the hashmap key for this collision inside the collision, for easy access to
    // free it up later.
    private Key2<Collider, Collider> key = new Key2<Collider, Collider>();

    public Collider getSource() {
        return source;
    }

    public Collider getTarget() {
        return target;
    }

    Key2<Collider, Collider> getKey() { return key; }

    void set(final Collider source, final Collider target) {
        this.source = source;
        this.target = target;
        key.set(source, target);
    }

    @Override
    public void reset() {
        source = target = null;
        key.reset();
    }
}
