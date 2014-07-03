package tiltadv.game.components;

import tiltadv.game.Entity;

import java.util.HashSet;
import java.util.Set;

/**
 * A collection of {@link Component}s. An {@link Entity} cannot contain more than one component with the same type as
 * another, as most components are singletons, but in the case where having multiple components of the same type makes
 * sense, you should use a component group instead.
 * <p/>
 * This class is abstract because Java generics use type-erasure, and if you just passed ComponentGroup{T}'s around, one
 * wouldn't be distinguishable from another. Instead, inherit from this class, for example,
 * {@code class ScriptComponents extends ComponentGroup<ScriptComponent>}
 */
public abstract class ComponentGroup<T extends Component> implements Component {

    private Set<Component> components;

    public ComponentGroup(final T... components) {
        this.components = new HashSet<Component>(components.length);
        for (Component component : components) {
            if (this.components.contains(component)) {
                throw new IllegalArgumentException("Duplicate component added to component group");
            }
            this.components.add(component);
        }
    }

    @Override
    public final void initialize(final Entity owner) {
        for (Component component : components) {
            component.initialize(owner);
        }
    }
}
