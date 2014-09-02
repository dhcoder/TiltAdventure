package dhcoder.libgdx.collision;

import dhcoder.support.math.IntCoord;
import dhcoder.support.memory.Poolable;

import java.util.ArrayList;
import java.util.Stack;

/**
 * A subdivision of collider objects whose collision should be tested amongst each other.
 * <p/>
 * Object collision is fundamentally an O(N²) operation (every object wants to check if it collides with every other
 * object). There are various optimizations for this problem, but what we're doing here is subdividing the overall
 * collision space into smaller regions, so we only have to test collisions within regions.
 * <p/>
 * We're still doing ~N² collision checks here, but where N is much smaller. For example, assume we had 50 objects
 * on screen, meaning we would normally do 2,500 collision checks! Now, assume we've broken up the screen into 10
 * regions, so there are about 5 objects per region. Now we're doing 5² * 10 checks, or only 250 checks. Breaking things
 * up into more regions can reduce this number even more dramatically.
 */
public final class CollisionRegion implements Poolable {

    private final ArrayList<Collider> colliders = new ArrayList<Collider>(4);
    private final Stack<Collider> queuedForRemoval = new Stack<Collider>();
    private final IntCoord coordinates = new IntCoord();
    private boolean isUpdating;
    private CollisionSystem system;

    public IntCoord getCoordinates() {
        return coordinates;
    }

    public CollisionRegion setCoordinates(final IntCoord coordinates) {
        this.coordinates.setFrom(coordinates);
        return this;
    }

    public CollisionRegion setCollisionSystem(final CollisionSystem system) {
        this.system = system;
        return this;
    }

    public boolean isEmpty() {
        return colliders.isEmpty();
    }

    public void addCollider(final Collider collider) {
        colliders.add(collider);
    }

    public void remove(final Collider collider) {
        if (isUpdating) {
            queuedForRemoval.push(collider);
            return;
        }

        colliders.remove(collider);
    }

    public void triggerCollisions() {
        isUpdating = true;
        int numColliders = colliders.size();
        for (int sourceIndex = 0; sourceIndex < numColliders; sourceIndex++) {
            Collider sourceCollider = colliders.get(sourceIndex);
            for (int targetIndex = 0; targetIndex < numColliders; ++targetIndex) {
                if (targetIndex == sourceIndex) {
                    continue;
                }
                Collider targetCollider = colliders.get(targetIndex);
                system.testForCollision(sourceCollider, targetCollider);
            }
        }
        isUpdating = false;

        while (!queuedForRemoval.empty()) {
            remove(queuedForRemoval.pop());
        }
    }

    @Override
    public void reset() {
        coordinates.reset();
        system = null;
        colliders.clear();
        queuedForRemoval.clear();
        isUpdating = false;
    }
}
