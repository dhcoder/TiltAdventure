package tiltadv.game.components.box2d;

import tiltadv.game.components.ComponentGroup;

/**
 * A collection of box2d fixtures.
 */
public final class FixtureComponents extends ComponentGroup<FixtureComponent> {

    public FixtureComponents(final FixtureComponent... components) {
        super(components);
    }
}
