package dhcoder.libgdx.entity;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import dhcoder.support.collection.ArrayMap;
import dhcoder.support.memory.Pool;
import dhcoder.support.time.Duration;

import java.util.List;
import java.util.Stack;

import static dhcoder.support.text.StringUtils.format;

/**
 * A class which manages of collection of entities.
 */
public final class EntityManager {

    private final Pool<Entity> entityPool;
    private final ArrayMap<Class, Pool> componentPools;
    private final Stack<Entity> queuedForRemoval;
    private boolean updating;

    public EntityManager(final int maxEntityCount) {
        entityPool = Pool.of(Entity.class, maxEntityCount);
        queuedForRemoval = new Stack<Entity>();
        queuedForRemoval.ensureCapacity(maxEntityCount / 10);
        componentPools = new ArrayMap<Class, Pool>(32);
    }

    public void preallocateComponents(final Class<? extends Component> componentClass) {
        preallocateComponents(componentClass, Pool.DEFAULT_CAPACITY);
    }

    public void preallocateComponents(final Class<? extends Component> componentClass, final int maxCount) {
        if (componentPools.containsKey(componentClass)) {
            throw new IllegalArgumentException(
                format("Duplicate request to preallocate component type {0}", componentClass));
        }

        componentPools.put(componentClass, Pool.of(componentClass, maxCount, false));
    }

    public Entity newEntity() {
        return entityPool.grabNew();
    }

    public <C extends Component> C newComponent(final Class<C> componentClass) {
        if (!componentPools.containsKey(componentClass)) {
            componentPools.put(componentClass, Pool.of(componentClass, Pool.DEFAULT_CAPACITY, true));
        }

        return (C)componentPools.get(componentClass).grabNew();
    }

    public void freeEntity(final Entity entity) {
        if (updating) {
            queuedForRemoval.push(entity);
            return;
        }

        freeEntityInternal(entity);
    }

    private void freeEntityInternal(final Entity entity) {
        entity.freeComponents(this);
        entityPool.free(entity);
    }

    void freeComponent(final Component component) {
        Class<? extends Component> componentClass = component.getClass();
        if (!componentPools.containsKey(componentClass)) {
            throw new IllegalArgumentException(
                format("Can't free component type {0} as we don't own it.", componentClass));
        }

        componentPools.get(componentClass).free(component);
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
            freeEntityInternal(queuedForRemoval.pop());
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
