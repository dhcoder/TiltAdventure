package tiltadv.entity;

import com.badlogic.gdx.graphics.g2d.Batch;
import dhcoder.support.immutable.ImmutableDuration;

/**
 * Abstract {@link Component} which provides default implementations for all methods, so you can only override the ones
 * you care about.
 */
public abstract class AbstractComponent implements Component {

    @Override
    public void initialize(final Entity owner) {}

    @Override
    public void update(final ImmutableDuration elapsedTime) {}

    @Override
    public void render(final Batch batch) {}

    @Override
    public void dispose() {}
}
