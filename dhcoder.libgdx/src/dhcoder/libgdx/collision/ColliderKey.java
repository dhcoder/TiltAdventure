package dhcoder.libgdx.collision;

import dhcoder.support.collection.Key2;

/**
 * Convenience class to represent a {@code Key2<Collider, Collider>}.
 */
final class ColliderKey extends Key2<Collider, Collider> {
    public ColliderKey() {}

    public ColliderKey(final Collider value1, final Collider value2) {
        super(value1, value2);
    }
}
