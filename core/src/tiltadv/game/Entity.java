package tiltadv.game;

import tiltadv.game.components.Component;
import tiltadv.game.components.SingletonComponent;
import tiltadv.util.Opt;

import java.util.HashMap;
import java.util.Map;

import static tiltadv.util.StringUtils.format;

/**
 * A skeletal game object whose behavior is implemented by {@link Component}s.
 */
public class Entity {

    // Map a component's type to the component itself
    private final Map<Class<? extends SingletonComponent>, SingletonComponent> components =
        new HashMap<Class<? extends SingletonComponent>, SingletonComponent>();

    public Entity(final SingletonComponent... components) {

        if (components.length == 0) {
            throw new IllegalArgumentException("Attempted to create Entity with no components");
        }

        for (SingletonComponent component : components) {
            if (this.components.get(component.getClass()) != null) {
                throw new IllegalArgumentException(
                    format("Attempted to add duplicate component type {0} to Entity", component.getClass()));
            }
            this.components.put(component.getClass(), component);
        }

        for (SingletonComponent component : components) {
            component.initialize(this);
        }
    }

    /**
     * Returns the component that matches the input type, if found.
     */
    @SuppressWarnings("unchecked") // We always map Class<T> to T, so the cast below is safe
    public <T extends SingletonComponent> Opt<T> getComponent(final Class<T> classType) {
        return new Opt((T)components.get(classType));
    }

    /**
     * Clear up any resources used by this entity.
     */
    public void dispose() {
        for (SingletonComponent component : components.values()) {
            component.dispose();
        }
    }
}
