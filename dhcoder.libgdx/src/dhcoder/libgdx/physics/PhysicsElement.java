package dhcoder.libgdx.physics;

/**
 * A tag for any class that has information it wants to update after the physics step is complete.
 */
public interface PhysicsElement {
    /**
     * Method to call after the physics update step is complete.
     */
    void syncWithPhysics();
}
