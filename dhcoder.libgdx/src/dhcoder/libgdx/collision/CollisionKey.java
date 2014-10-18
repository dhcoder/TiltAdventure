package dhcoder.libgdx.collision;

import dhcoder.support.collection.Key2;

/**
 * Convenience class to represent a {@code Key2<Collider, Collider>}.
 */
final class CollisionKey extends Key2<Collider, Collider> {
    public CollisionKey() {}

    public CollisionKey(final Collider value1, final Collider value2) {
        super(value1, value2);
    }
}
