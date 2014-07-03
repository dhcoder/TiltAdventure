package tiltadv.game.components;

import tiltadv.game.Entity;

import java.util.HashSet;
import java.util.Set;

/**
 * A collection of {@link Component}s.
 * <p/>
 * An {@link Entity} can only directly contain {@link SingletonComponent}s, but if it makes sense to add multiple
 * components of the same type, you should do so using a component group.
 * <p/>
 * This class is abstract because Java generics use type-erasure, and if you just passed {@code ComponentGroup<T>}'s
 * around, one wouldn't be distinguishable from another. You can get around this limitation by inheriting from this
 * class, for example:
 * <pre>
 * class ScriptComponents extends {@code ComponentGroup<ScriptComponent>} {
 *    public ScriptComponents(final ScriptComponent... components) {
 *       super(components);
 *    }
 * }
 * </pre>
 */
public abstract class ComponentGroup<T extends Component> implements SingletonComponent {

    private final Set<Component> components;

    public ComponentGroup(final T... components) {
        this.components = new HashSet<Component>(components.length);
        for (Component component : components) {
            if (this.components.contains(component)) {
                throw new IllegalArgumentException("Duplicate component added to component group");
            }
            this.components.add(component);
        }
    }

    public Iterable<Component> getComponents() {
        return components;
    }

    @Override
    public final void initialize(final Entity owner) {
        for (Component component : components) {
            component.initialize(owner);
        }
    }
}
