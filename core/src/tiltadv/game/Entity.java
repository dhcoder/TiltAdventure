package tiltadv.game;

import tiltadv.game.components.Component;

import java.util.HashMap;
import java.util.Map;

import static tiltadv.util.StringUtils.format;

/**
 * A skeletal game object whose behavior is implemented by {@link Component}s.
 */
public class Entity {
    // Map a component's type to the component itself
    private final Map<Class<? extends Component>, Component> components =
        new HashMap<Class<? extends Component>, Component>();

    public Entity(final Component... components) {
        for (Component component : components) {

            if (this.components.get(component.getClass()) != null) {
                throw new IllegalArgumentException(
                    format("Attempted to add duplicate component type {0} to Entity", component.getClass()));
            }

            this.components.put(component.getClass(), component);
        }

        for (Component component : components) {
            component.initialize(this);
        }
    }

    @SuppressWarnings("unchecked") // We always map Class<T> to T, so the cast below is safe
    public <T extends Component> T getComponent(final Class<T> classType) {
        return (T)components.get(classType);
    }
}
