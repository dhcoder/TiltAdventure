package tiltadv.game;

import tiltadv.game.components.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * A skeletal game object whose behavior is implemented by {@link Component}s.
 */
public class Entity {
    private Map<Class<? extends Component>, Component> components =
        new HashMap<Class<? extends Component>, Component>();

    public Entity(Component... components) {
        for (Component component : components) {
            this.components.put(component.getClass(), component);
        }

        for (Component component : components) {
            component.initialize(this);
        }
    }

    public <T extends Component> T getComponent(Class<T> classType) {
        return (T) components.get(classType);
    }
}
