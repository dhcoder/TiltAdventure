package dhcoder.libgdx.entity;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import dhcoder.support.collection.ArrayMap;
import dhcoder.support.collection.IntKey;
import dhcoder.support.memory.Pool;
import dhcoder.support.opt.Opt;
import dhcoder.support.time.Duration;

import java.util.List;
import java.util.Stack;

import static dhcoder.support.text.StringUtils.format;

/**
 * A class which manages of collection of entities.
 */
public final class EntityManager {

    public static interface EntityCreator {
        void initialize(Entity entity);
    }

    private final Pool<Entity> entityPool;
    private final ArrayMap<Class, Pool> componentPools;
    private final ArrayMap<IntKey, EntityCreator> templates;
    private final Stack<Entity> queuedForRemoval;
    private final Pool<IntKey> keyPool = Pool.of(IntKey.class, 1);
    private final Pool<Opt> optPool = Pool.of(Opt.class, 1);

    public EntityManager(final int maxEntityCount) {
        entityPool = Pool.of(Entity.class, maxEntityCount);
        queuedForRemoval = new Stack<Entity>();
        queuedForRemoval.ensureCapacity(maxEntityCount / 10);
        componentPools = new ArrayMap<Class, Pool>(32);
        templates = new ArrayMap<IntKey, EntityCreator>();
    }

    public void registerTemplate(final Enum id, final EntityCreator entityCreator) {
        Opt<EntityCreator> entityCreatorOpt = optPool.grabNew();
        getEntityCreator(id, entityCreatorOpt);
        optPool.free(entityCreatorOpt);

        if (entityCreatorOpt.hasValue()) {
            throw new IllegalArgumentException(format("Attempt to register duplicate entity template id {0}", id));
        }

        templates.put(new IntKey(id.ordinal()), entityCreator);
    }

    /**
     * Using {@link #newComponent(Class)} will generate new components from a dynamically increasing pool, but if you
     * know ahead of time how many components you'll need, you can preallocate them here.
     */
    public void preallocate(final Class<? extends Component> componentClass, final int maxCount) {
        if (componentPools.containsKey(componentClass)) {
            throw new IllegalArgumentException(
                format("Duplicate request to preallocate component type {0}", componentClass));
        }

        componentPools.put(componentClass, Pool.of(componentClass, maxCount));
    }

    public Entity newEntityFromTemplate(final Enum id) {
        Opt<EntityCreator> entityCreatorOpt = optPool.grabNew();
        getEntityCreator(id, entityCreatorOpt);

        if (!entityCreatorOpt.hasValue()) {
            optPool.free(entityCreatorOpt);
            throw new IllegalArgumentException(format("Attempt to create entity from invalid template id {0}", id));
        }

        Entity entity = newEntity();
        entityCreatorOpt.getValue().initialize(entity);
        optPool.free(entityCreatorOpt);

        return entity;
    }

    public Entity newEntity() {
        return entityPool.grabNew();
    }

    public <C extends Component> C newComponent(final Class<C> componentClass) {
        if (!componentPools.containsKey(componentClass)) {
            componentPools.put(componentClass, Pool.of(componentClass, Pool.DEFAULT_CAPACITY).setResizable(true));
        }

        return (C)componentPools.get(componentClass).grabNew();
    }

    public void freeEntity(final Entity entity) {
        queuedForRemoval.push(entity);
    }

    public void update(final Duration elapsedTime) {
        // Kill any dead objects from the last cycle
        while (!queuedForRemoval.empty()) {
            freeEntityInternal(queuedForRemoval.pop());
        }

        List<Entity> entities = entityPool.getItemsInUse();
        int numEntities = entities.size();
        for (int i = 0; i < numEntities; ++i) {
            entities.get(i).update(elapsedTime);
        }
    }

    public void render(final SpriteBatch batch) {
        List<Entity> entities = entityPool.getItemsInUse();
        int numEntities = entities.size(); // Simple iteration to avoid Iterator allocation

        for (int i = 0; i < numEntities; ++i) {
            entities.get(i).render(batch);
        }
    }

    void freeComponent(final Component component) {
        Class<? extends Component> componentClass = component.getClass();
        if (!componentPools.containsKey(componentClass)) {
            throw new IllegalArgumentException(
                format("Can't free component type {0} as we don't own it.", componentClass));
        }

        componentPools.get(componentClass).free(component);
    }

    private void getEntityCreator(final Enum id, final Opt<EntityCreator> entityCreatorOpt) {
        IntKey key = keyPool.grabNew().set(id.ordinal());
        if (templates.containsKey(key)) {
            entityCreatorOpt.set(templates.get(key));
        }
        keyPool.free(key);
    }

    private void freeEntityInternal(final Entity entity) {
        entity.freeComponents(this);
        entityPool.free(entity);
    }
}
