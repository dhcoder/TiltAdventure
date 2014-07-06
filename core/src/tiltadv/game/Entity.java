package tiltadv.game;

import tiltadv.game.components.Component;
import tiltadv.util.opt.Opt;

import java.util.ArrayList;
import java.util.List;

/**
 * A skeletal game object whose behavior is implemented by {@link Component}s.
 */
public class Entity {

    // Map a component's type to the component itself
    private final List<Component> components = new ArrayList<Component>();

    public Entity(final Component... components) {

        if (components.length == 0) {
            throw new IllegalArgumentException("Attempted to create Entity with no components");
        }

        for (Component component : components) {
            this.components.add(component);
        }

        initialize();
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
     * Clear up any resources used by this entity.
     */
    public void dispose() {
        for (Component component : components) {
            component.dispose();
        }
    }

    private void initialize() {
        for (Component component : components) {
            component.initialize(this);
        }
    }
}
