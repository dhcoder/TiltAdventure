package tiltadv.game.components;

import tiltadv.game.Entity;

/**
 * A {@link Component} that an {@link Entity} should only have one of at any time.
 * <p/>
 * Most components are actually singleton components, but in the case where a component type can support more than one
 * instance on the same {@link Entity}, you should wrap them in a {@link ComponentGroup} and add that instead.
 */
public interface SingletonComponent extends Component {}
