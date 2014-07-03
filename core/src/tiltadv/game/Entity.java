package tiltadv.game;

import tiltadv.game.components.Component;
import tiltadv.game.components.SingletonComponent;

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

    @SuppressWarnings("unchecked") // We always map Class<T> to T, so the cast below is safe
    public <T extends SingletonComponent> T getComponent(final Class<T> classType) {
        return (T)components.get(classType);
    }
}
