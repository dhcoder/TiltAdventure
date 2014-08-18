package dhcoder.libgdx.entity;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import dhcoder.support.memory.Pool;
import dhcoder.support.time.Duration;

import java.util.List;
import java.util.Stack;

/**
 * A class which manages of collection of entities.
 */
public final class EntityManager {

    private final Pool<Entity> entityPool;
    private final Stack<Entity> queuedForRemoval;
    private boolean updating;

    public EntityManager(final int maxEntityCount) {
        entityPool = Pool.of(Entity.class, maxEntityCount);
        queuedForRemoval = new Stack<Entity>();
        queuedForRemoval.ensureCapacity(maxEntityCount / 10);
    }

    public Entity newEntity() {
        return entityPool.grabNew();
    }

    public void free(final Entity entity) {
        if (updating) {
            queuedForRemoval.push(entity);
            return;
        }

        entityPool.free(entity);
    }

    public void update(final Duration elapsedTime) {

        if (updating) {
            throw new IllegalStateException("Attempt to update entities while an update is already in progress!");
        }

        updating = true;
        {
            List<Entity> entities = entityPool.getItemsInUse();
            int numEntities = entities.size();
            for (int i = 0; i < numEntities; ++i) {
                entities.get(i).update(elapsedTime);
            }
        }
        updating = false;

        while (!queuedForRemoval.empty()) {
            free(queuedForRemoval.pop());
        }
    }

    public void render(final SpriteBatch batch) {
        List<Entity> entities = entityPool.getItemsInUse();
        int numEntities = entities.size(); // Simple iteration to avoid Iterator allocation

        for (int i = 0; i < numEntities; ++i) {
            entities.get(i).render(batch);
        }
    }
}
