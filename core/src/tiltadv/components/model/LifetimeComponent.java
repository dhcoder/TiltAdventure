package tiltadv.components.model;

import dhcoder.libgdx.entity.AbstractComponent;
import dhcoder.libgdx.entity.Entity;
import dhcoder.support.time.Duration;

/**
 * Component that regulates the lifetime of an object, killing it after a certain about of time passes.
 */
public final class LifetimeComponent extends AbstractComponent {

    private final Duration elapsedSoFar = Duration.zero();
    private final Duration lifetime = Duration.zero();
    private Entity owner;

    @Override
    public void initialize(final Entity owner) {
        this.owner = owner;
    }

    @Override
    public void update(final Duration elapsedTime) {
        elapsedSoFar.add(elapsedTime);
        if (elapsedSoFar.getSeconds() > lifetime.getSeconds()) {
            owner.getManager().freeEntity(owner);
        }
    }

    @Override
    public void reset() {
        owner = null;
        elapsedSoFar.reset();
        lifetime.reset();
    }

    public LifetimeComponent setLifetime(final Duration duration) {
        this.lifetime.setFrom(duration);
        return this;
    }
}
