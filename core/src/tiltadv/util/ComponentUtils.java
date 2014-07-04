package tiltadv.util;

import tiltadv.game.Entity;
import tiltadv.game.components.Component;

import java.util.List;

import static tiltadv.util.StringUtils.format;

public final class ComponentUtils {

    /**
     * Require that there only be exactly one instance of the specified {@link Component} type on an owning {@link
     * Entity}. This is a useful way for a component to assert that it, itself, is a singleton within the entity,
     * when having multiple instances of it would not make sense.
     *
     * @return the singleton component, in case the calling class needs a reference to it.
     * @throws IllegalStateException if there is more than one component that matches the class type parameter.
     */
    public static Component requireSingleInstance(final Entity entity, final Class<? extends Component> classType)
        throws IllegalStateException {

        List<? extends Component> matchingComponents = entity.getComponents(classType);
        if (matchingComponents.size() != 1) {
            throw new IllegalStateException(
                format("Entity has {0} instances of component {1}, should only have 1", matchingComponents.size(),
                    classType));
        }

        return matchingComponents.get(0);
    }

    /**
     * Require that there be at least one instance of the specified {@link Component} type on an owning {@link Entity}.
     * This is a useful way for one component that depends on another to assert that the data it needs is there.
     *
     * @return the matching components, so that the calling class doesn't have to re-request it.
     * @throws IllegalStateException if there aren't any components that match the class type parameter.
     */
    public static List<? extends Component> requireComponents(final Entity entity,
        final Class<? extends Component> classType) throws IllegalStateException {

        List<? extends Component> matchingComponents = entity.getComponents(classType);
        if (matchingComponents.size() == 0) {
            throw new IllegalStateException(
                format("Entity doesn't have any instances of {0}, should have at least 1", classType));
        }

        return matchingComponents;
    }
}
