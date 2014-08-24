package dhcoder.libgdx.collision;

/**
 * Interface for any class that wants to listen to collision-related events.
 */
public interface CollisionListener {
    void onCollided(Collision collision);
    void onSeparated(Collision collision);
}
