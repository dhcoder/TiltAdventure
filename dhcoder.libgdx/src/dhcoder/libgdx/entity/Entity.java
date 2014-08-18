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
    public <T extends Component> Opt<T> getComponent(final Class<T> classType) {
        for (Component component : components) {
            if (classType.isInstance(component)) {
                return Opt.of((T)component);
            }
        }

        return Opt.withNoValue();
    }

    /**
     * Returns the components that match the input type, although this may be an empty list.
     */
    @SuppressWarnings("unchecked") // (T) cast is safe because of instanceof check
    public <T extends Component> List<T> getComponents(final Class<T> classType) {
        List<T> matchingComponents = new ArrayList<T>();
        for (Component component : components) {
            if (classType.isInstance(component)) {
                matchingComponents.add((T)component);
            }
        }

        return matchingComponents;
    }

    /**
     * Require that there only be exactly one instance of the specified {@link Component} type on this entity. This
     * is a useful way for a component to assert that it, itself, is a singleton within the entity,
     * when having multiple instances of it would not make sense.
     * <p/>
     * You should consider using {@link #requireComponent(Class)} instead because it often has the same results and is
     * a bit more lightweight than this method.
     *
     * @return the singleton component, in case the calling class needs a reference to it.
     * @throws IllegalStateException if there is more than one component that matches the class type parameter.
     */
    public <T extends Component> T requireSingleInstance(final Class<T> classType) throws IllegalStateException {

        List<T> matchingComponents = getComponents(classType);
        if (matchingComponents.size() != 1) {
            throw new IllegalStateException(
                format("Entity has {0} instances of component {1}, should only have 1", matchingComponents.size(),
                    classType));
        }

        return matchingComponents.get(0);
    }

    /**
     * Require that there be at least one instance of the specified {@link Component} on this entity. This is a
     * useful way for one component that depends on another to assert that the data it needs is there.
     *
     * @return the matching components
     * @throws IllegalStateException if there aren't any components that match the class type parameter.
     */
    public <T extends Component> List<T> requireComponents(final Class<T> classType) throws IllegalStateException {

        List<T> matchingComponents = getComponents(classType);
        if (matchingComponents.size() == 0) {
            throw new IllegalStateException(
                format("Entity doesn't have any instances of {0}, should have at least 1", classType));
        }

        return matchingComponents;
    }

    /**
     * Require that there be at least one instance of the specified {@link Component} on this entity, and return the
     * first one. This method is similar to {@link #requireSingleInstance(Class)} but is a little bit more
     * lightweight because it doesn't have to loop through all components to verify that there's not more than one.
     *
     * @return the first matching component
     * @throws IllegalStateException if there aren't any components that match the class type parameter.
     */
    public <T extends Component> T requireComponent(final Class<T> classType) throws IllegalStateException {

        Opt<T> componentOpt = getComponent(classType);

        if (!componentOpt.hasValue()) {
            throw new IllegalStateException(
                format("Entity doesn't have any instances of {0}, should have at least 1", classType));
        }

        return componentOpt.getValue();
    }

    /**
     * Clear up any resources used by this entity.
     */
    void dispose() {
        int numComponents = components.size(); // Simple iteration to avoid Iterator allocation
        for (int i = 0; i < numComponents; ++i) {
            components.get(i).dispose();
        }
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
        int numComponents = components.size(); // Simple iteration to avoid Iterator allocation
        for (int i = 0; i < numComponents; ++i) {
            components.get(i).render(batch);
        }
    }

    @Override
    public void reset() {
        dispose();
        components.clear();
        initialized = false;
    }

    private void initialize() {

        assert !initialized;

        for (Component component : components) {
            component.initialize(this);
        }
        initialized = true;
    }
}
