package dhcoder.libgdx.entity;

import com.badlogic.gdx.graphics.g2d.Batch;
import dhcoder.support.memory.Poolable;
import dhcoder.support.opt.Opt;
import dhcoder.support.time.Duration;

import java.util.ArrayList;
import java.util.List;

import static dhcoder.support.text.StringUtils.format;

/**
 * A skeletal game object whose behavior is implemented by {@link Component}s.
 */
public final class Entity implements Poolable {

    // Map a component's type to the component itself
    private final List<Component> components = new ArrayList<Component>();
    private boolean initialized;

    /**
     * Restricted access - use {@link EntityManager#newEntity} instead.
     */
    private Entity() {}

    /**
     * Add a component to the entity. You can safely add components after you've created an entity but before you call
     * {@link #update(Duration)} for the very first time.
     *
     * @throws IllegalStateException if you try to add a component to an entity that's already in use (that is, has
     *                               been updated at least once).
     */
    public void addComponent(final Component component) {
        if (initialized) {
            throw new IllegalStateException("Can't add a component to an Entity that's already in use.");
        }

        this.components.add(component);
    }

    /**
     * Returns the component that matches the input type, if found.
     */
    @SuppressWarnings("unchecked") // (T) cast is safe because of instanceof check
    public <T extends Component> void getComponent(final Class<T> classType, final Opt<T> outComponent) {
        outComponent.clear();
        int numComponets = components.size();
        for (int i = 0; i < numComponets; i++) {
            Component component = components.get(i);
            if (classType.isInstance(component)) {
                outComponent.set((T)component);
            }
        }
    }

    /**
     * Require that there be at least one instance of the specified {@link Component} on this entity, and return the
     * first one.
     *
     * @return the first matching component
     * @throws IllegalStateException if there aren't any components that match the class type parameter.
     */
    public <T extends Component> T requireComponent(final Class<T> classType) throws IllegalStateException {
        int numComponets = components.size();
        for (int i = 0; i < numComponets; i++) {
            Component component = components.get(i);
            if (classType.isInstance(component)) {
                return (T)component;
            }
        }

        throw new IllegalStateException(
            format("Entity doesn't have any instances of {0}, should have at least 1", classType));
    }

    /**
     * Update this entity. The passed in time is in seconds.
     */
    public void update(final Duration elapsedTime) {
        if (!initialized) {
            initialize();
        }

        int numComponents = components.size(); // Simple iteration to avoid Iterator allocation
        for (int i = 0; i < numComponents; ++i) {
            components.get(i).update(elapsedTime);
        }
    }

    /**
     * Render this entity, via a {@link Batch}.
     */
    public void render(final Batch batch) {
        if (!initialized) {
            // TODO: Remote Entity.render!
            // Some Entities are created dynamically and then get asked to render before they are ready. An upcoming
            // refactoring will fix this by moving rendering out of Entity into another class (and each entity will be
            // responsible to request that their sprite gets rendered each frame)
            return;
        }

        int numComponents = components.size(); // Simple iteration to avoid Iterator allocation
        for (int i = 0; i < numComponents; ++i) {
            components.get(i).render(batch);
        }
    }

    @Override
    public void reset() {
        components.clear();
        initialized = false;
    }

    void freeComponents(final EntityManager entityManager) {
        int numComponents = components.size(); // Simple iteration to avoid Iterator allocation
        for (int i = 0; i < numComponents; ++i) {
            entityManager.freeComponent(components.get(i));
        }
    }

    private void initialize() {
        assert !initialized;

        final int numComponents = components.size();
        for (int i = 0; i < numComponents; i++) {
            Component component = components.get(i);
            component.initialize(this);
        }
        initialized = true;
    }
}
