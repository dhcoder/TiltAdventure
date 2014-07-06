package d9n.utils.entity;

/**
 * Abstract {@link Component} which provides default implementations for all methods, so you can only override the ones
 * you care about.
 */
public abstract class AbstractComponent implements Component {

    @Override
    public void initialize(final Entity owner) {}

    @Override
    public void dispose() {}
}
