package dhcoder.libgdx.collision;

import com.badlogic.gdx.math.Vector2;
import dhcoder.support.memory.Poolable;

import static dhcoder.libgdx.collision.shape.ShapeUtils.getRepulsion;

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

    public void getRepulsionBetweenColliders(final Vector2 outRepulsion) {
        Vector2 sourceCurrPos = source.getCurrPosition();
        Vector2 sourceLastPos = source.getLastPosition();
        Vector2 targetCurrPos = target.getCurrPosition();
        Vector2 targetLastPos = target.getLastPosition();
        getRepulsion(source.getShape(), sourceLastPos.x, sourceLastPos.y, sourceCurrPos.x, sourceCurrPos.y,
            target.getShape(), targetLastPos.x, targetLastPos.y, targetCurrPos.x, targetCurrPos.y, outRepulsion);

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

